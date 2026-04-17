package com.example.helpdesk.config;

import com.example.helpdesk.entity.Department;
import com.example.helpdesk.entity.FeedbackCategory;
import com.example.helpdesk.entity.Room;
import com.example.helpdesk.entity.Student;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.repository.FeedbackCategoryRepository;
import com.example.helpdesk.repository.RoomRepository;
import com.example.helpdesk.repository.StudentRepository;
import com.example.helpdesk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer to create sample data for testing.
 * Creates demo users and sample departments/categories.
 * 
 * @author Facility Helpdesk Team
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FeedbackCategoryRepository categoryRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            FeedbackCategoryRepository categoryRepository,
            RoomRepository roomRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Initializing sample data...");
        createUsers();
        upgradePasswords(); // Upgrade any plaintext passwords to BCrypt
        Department itDept = createDepartments();
        createRooms(itDept);
        createCategories();
        logger.info("Data initialization completed");
    }

    private void upgradePasswords() {
        java.util.List<User> users = userRepository.findAll();
        int upgradedCount = 0;
        for (User user : users) {
            // Check if password is NOT a BCrypt hash (BCrypt hashes start with $2a$ or $2b$)
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                logger.info("Upgrading plaintext password to BCrypt for user: {}", user.getEmail());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                upgradedCount++;
            }
        }
        if (upgradedCount > 0) {
            logger.info("Upgraded {} plaintext passwords to BCrypt.", upgradedCount);
        }
    }

    private void createUsers() {
        // Demo student
        if (!userRepository.existsByEmail("demo@fpt.edu.vn")) {
            User demoUser = User.builder()
                    .fullName("Nguyễn Văn Demo")
                    .email("demo@fpt.edu.vn")
                    .password(passwordEncoder.encode("demo123"))
                    .role("STUDENT")
                    .build();
            demoUser = userRepository.save(demoUser);

            Student student = Student.builder()
                    .studentCode("SE12345")
                    .className("SE1701")
                    .user(demoUser)
                    .build();
            studentRepository.save(student);
            logger.info("Created demo student: demo@fpt.edu.vn / demo123");
        }

        // Additional student
        if (!userRepository.existsByEmail("student1@fpt.edu.vn")) {
            User student1 = User.builder()
                    .fullName("Trần Thị Hoa")
                    .email("student1@fpt.edu.vn")
                    .password(passwordEncoder.encode("123456"))
                    .role("STUDENT")
                    .build();
            student1 = userRepository.save(student1);

            Student studentProfile1 = Student.builder()
                    .studentCode("SE12346")
                    .className("SE1702")
                    .user(student1)
                    .build();
            studentRepository.save(studentProfile1);
        }

        // Staff user
        if (!userRepository.existsByEmail("staff@fpt.edu.vn")) {
            User staffUser = User.builder()
                    .fullName("Lê Văn Nam")
                    .email("staff@fpt.edu.vn")
                    .password(passwordEncoder.encode("staff123"))
                    .role("STAFF")
                    .build();
            userRepository.save(staffUser);
            logger.info("Created staff: staff@fpt.edu.vn / staff123");
        }

        // Admin user
        if (!userRepository.existsByEmail("admin@fpt.edu.vn")) {
            User adminUser = User.builder()
                    .fullName("Admin User")
                    .email("admin@fpt.edu.vn")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build();
            userRepository.save(adminUser);
            logger.info("Created admin: admin@fpt.edu.vn / admin123");
        }
    }

    private Department createDepartments() {
        if (departmentRepository.count() == 0) {
            Department itDept = Department.builder()
                    .name("IT Department")
                    .location("Building A, Floor 3")
                    .build();
            itDept = departmentRepository.save(itDept);

            departmentRepository.save(Department.builder()
                    .name("Facilities Management").location("Building B, Floor 1").build());
            departmentRepository.save(Department.builder()
                    .name("Human Resources").location("Building C, Floor 2").build());
            departmentRepository.save(Department.builder()
                    .name("Library").location("Building D, Floor 1").build());

            logger.info("Created sample departments");
            return itDept;
        }
        return departmentRepository.findByName("IT Department")
                .orElse(departmentRepository.findAll().get(0));
    }

    private void createRooms(Department itDept) {
        if (roomRepository.count() == 0) {
            roomRepository.save(Room.builder().roomName("Lab 301").department(itDept).build());
            roomRepository.save(Room.builder().roomName("Lab 302").department(itDept).build());

            Department facilitiesDept = departmentRepository.findByName("Facilities Management").orElse(null);
            if (facilitiesDept != null) {
                roomRepository.save(Room.builder().roomName("Meeting Room 101").department(facilitiesDept).build());
            }
            logger.info("Created sample rooms");
        }
    }

    private void createCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(FeedbackCategory.builder()
                    .name("Hardware Issue").description("Problems with computers, printers, or other hardware").slaHours(24).build());
            categoryRepository.save(FeedbackCategory.builder()
                    .name("Software Issue").description("Problems with software applications or systems").slaHours(12).build());
            categoryRepository.save(FeedbackCategory.builder()
                    .name("Facility Maintenance").description("Issues with rooms, furniture, or building facilities").slaHours(48).build());
            categoryRepository.save(FeedbackCategory.builder()
                    .name("Network Issue").description("Internet connectivity or network problems").slaHours(6).build());
            categoryRepository.save(FeedbackCategory.builder()
                    .name("Electrical Issue").description("Power outlets, lighting, or electrical problems").slaHours(12).build());
            categoryRepository.save(FeedbackCategory.builder()
                    .name("Cleaning Request").description("Room cleaning or maintenance requests").slaHours(24).build());
            logger.info("Created sample categories");
        }
    }
}
