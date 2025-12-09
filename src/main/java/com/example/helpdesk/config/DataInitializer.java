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
import org.springframework.stereotype.Component;

/**
 * Data initializer to create sample data for testing.
 * Creates a demo user and sample departments/categories.
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

    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            FeedbackCategoryRepository categoryRepository,
            RoomRepository roomRepository,
            StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Initializing sample data...");

        // Create users
        createUsers();
        
        // Create departments
        Department itDept = createDepartments();
        
        // Create rooms
        createRooms(itDept);
        
        // Create categories
        createCategories();

        logger.info("Data initialization completed");
    }

    /**
     * Create sample users (students and staff).
     */
    private void createUsers() {
        // Create demo student user
        if (!userRepository.existsByEmail("demo@fpt.edu.vn")) {
            User demoUser = User.builder()
                    .fullName("Nguyễn Văn Demo")
                    .email("demo@fpt.edu.vn")
                    .password("demo123")  // Plain text password (no encryption)
                    .role("STUDENT")
                    .build();
            demoUser = userRepository.save(demoUser);
            
            // Create student profile
            Student student = Student.builder()
                    .studentCode("SE12345")
                    .className("SE1701")
                    .user(demoUser)
                    .build();
            studentRepository.save(student);
            
            logger.info("Created demo student user: demo@fpt.edu.vn / demo123");
        }

        // Create additional student users
        if (!userRepository.existsByEmail("student1@fpt.edu.vn")) {
            User student1 = User.builder()
                    .fullName("Trần Thị Hoa")
                    .email("student1@fpt.edu.vn")
                    .password("123456")  // Plain text password (no encryption)
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

        // Create staff user
        if (!userRepository.existsByEmail("staff@fpt.edu.vn")) {
            User staffUser = User.builder()
                    .fullName("Lê Văn Nam")
                    .email("staff@fpt.edu.vn")
                    .password("staff123")  // Plain text password (no encryption)
                    .role("STAFF")
                    .build();
            userRepository.save(staffUser);
            logger.info("Created staff user: staff@fpt.edu.vn / staff123");
        }

        // Create admin user
        if (!userRepository.existsByEmail("admin@fpt.edu.vn")) {
            User adminUser = User.builder()
                    .fullName("Admin User")
                    .email("admin@fpt.edu.vn")
                    .password("admin123")  // Plain text password (no encryption)
                    .role("ADMIN")
                    .build();
            userRepository.save(adminUser);
            logger.info("Created admin user: admin@fpt.edu.vn / admin123");
        }
    }

    /**
     * Create sample departments.
     * 
     * @return IT Department for use in creating rooms
     */
    private Department createDepartments() {
        if (departmentRepository.count() == 0) {
            Department itDept = Department.builder()
                    .name("IT Department")
                    .location("Building A, Floor 3")
                    .build();
            itDept = departmentRepository.save(itDept);

            Department facilitiesDept = Department.builder()
                    .name("Facilities Management")
                    .location("Building B, Floor 1")
                    .build();
            departmentRepository.save(facilitiesDept);

            Department hrDept = Department.builder()
                    .name("Human Resources")
                    .location("Building C, Floor 2")
                    .build();
            departmentRepository.save(hrDept);

            Department libraryDept = Department.builder()
                    .name("Library")
                    .location("Building D, Floor 1")
                    .build();
            departmentRepository.save(libraryDept);

            logger.info("Created sample departments");
            return itDept;
        }
        return departmentRepository.findByName("IT Department")
                .orElse(departmentRepository.findAll().get(0));
    }

    /**
     * Create sample rooms for departments.
     */
    private void createRooms(Department itDept) {
        if (roomRepository.count() == 0) {
            Room room1 = Room.builder()
                    .roomName("Lab 301")
                    .department(itDept)
                    .build();
            roomRepository.save(room1);

            Room room2 = Room.builder()
                    .roomName("Lab 302")
                    .department(itDept)
                    .build();
            roomRepository.save(room2);

            Department facilitiesDept = departmentRepository.findByName("Facilities Management")
                    .orElse(null);
            if (facilitiesDept != null) {
                Room room3 = Room.builder()
                        .roomName("Meeting Room 101")
                        .department(facilitiesDept)
                        .build();
                roomRepository.save(room3);
            }

            logger.info("Created sample rooms");
        }
    }

    /**
     * Create sample feedback categories.
     */
    private void createCategories() {
        if (categoryRepository.count() == 0) {
            FeedbackCategory cat1 = FeedbackCategory.builder()
                    .name("Hardware Issue")
                    .description("Problems with computers, printers, or other hardware equipment")
                    .slaHours(24)
                    .build();
            categoryRepository.save(cat1);

            FeedbackCategory cat2 = FeedbackCategory.builder()
                    .name("Software Issue")
                    .description("Problems with software applications or systems")
                    .slaHours(12)
                    .build();
            categoryRepository.save(cat2);

            FeedbackCategory cat3 = FeedbackCategory.builder()
                    .name("Facility Maintenance")
                    .description("Issues with rooms, furniture, or building facilities")
                    .slaHours(48)
                    .build();
            categoryRepository.save(cat3);

            FeedbackCategory cat4 = FeedbackCategory.builder()
                    .name("Network Issue")
                    .description("Internet connectivity or network problems")
                    .slaHours(6)
                    .build();
            categoryRepository.save(cat4);

            FeedbackCategory cat5 = FeedbackCategory.builder()
                    .name("Electrical Issue")
                    .description("Power outlets, lighting, or electrical problems")
                    .slaHours(12)
                    .build();
            categoryRepository.save(cat5);

            FeedbackCategory cat6 = FeedbackCategory.builder()
                    .name("Cleaning Request")
                    .description("Room cleaning or maintenance requests")
                    .slaHours(24)
                    .build();
            categoryRepository.save(cat6);

            logger.info("Created sample categories");
        }
    }
}

