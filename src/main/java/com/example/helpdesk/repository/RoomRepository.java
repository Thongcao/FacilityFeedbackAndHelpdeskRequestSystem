package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Room entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Facility Helpdesk Team
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Find all rooms belonging to a specific department.
     * 
     * @param departmentId the department ID to search for
     * @return list of rooms in the department
     */
    List<Room> findByDepartmentId(Long departmentId);

    /**
     * Find room by name and department.
     * 
     * @param roomName the room name to search for
     * @param departmentId the department ID
     * @return list of matching rooms
     */
    List<Room> findByRoomNameAndDepartmentId(String roomName, Long departmentId);
}



