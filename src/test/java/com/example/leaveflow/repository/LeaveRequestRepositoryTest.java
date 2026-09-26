package com.example.leaveflow.repository;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class LeaveRequestRepositoryTest {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindLeaveRequestsByEmployeeId() {

        // Employee 1
        Employee employee1 = new Employee();
        employee1.setName("Employee One");
        employee1.setEmail("employee1@test.com");

        // Employee 2
        Employee employee2 = new Employee();
        employee2.setName("Employee Two");
        employee2.setEmail("employee2@test.com");

        entityManager.persist(employee1);
        entityManager.persist(employee2);

        // Leave belonging to Employee 1
        LeaveRequest leave1 = new LeaveRequest();
        leave1.setStartDate(LocalDate.of(2026, 10, 1));
        leave1.setEndDate(LocalDate.of(2026, 10, 3));
        leave1.setReason("Vacation");
        leave1.setStatus(LeaveStatus.PENDING);
        leave1.setEmployee(employee1);

        // Another leave belonging to Employee 1
        LeaveRequest leave2 = new LeaveRequest();
        leave2.setStartDate(LocalDate.of(2026, 11, 1));
        leave2.setEndDate(LocalDate.of(2026, 11, 2));
        leave2.setReason("Personal");
        leave2.setStatus(LeaveStatus.PENDING);
        leave2.setEmployee(employee1);

        // Leave belonging to Employee 2
        LeaveRequest leave3 = new LeaveRequest();
        leave3.setStartDate(LocalDate.of(2026, 12, 1));
        leave3.setEndDate(LocalDate.of(2026, 12, 2));
        leave3.setReason("Medical");
        leave3.setStatus(LeaveStatus.PENDING);
        leave3.setEmployee(employee2);

        entityManager.persistAndFlush(leave1);
        entityManager.persistAndFlush(leave2);
        entityManager.persistAndFlush(leave3);

        // Query repository
        Page<LeaveRequest> result =
                leaveRequestRepository.findByEmployeeId(
                        employee1.getId(),
                        PageRequest.of(0, 10)
                );

        // Verify
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void shouldFindLeaveRequestsByEmployeeManagerId() {

        Employee manager = new Employee();
        manager.setName("Manager");
        manager.setEmail("manager@test.com");

        Employee employee1 = new Employee();
        employee1.setName("Employee One");
        employee1.setEmail("employee1@test.com");
        employee1.setManager(manager);

        Employee employee2 = new Employee();
        employee2.setName("Employee Two");
        employee2.setEmail("employee2@test.com");
        employee2.setManager(manager);

        Employee otherEmployee = new Employee();
        otherEmployee.setName("Other Employee");
        otherEmployee.setEmail("other@test.com");

        entityManager.persist(manager);
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.persist(otherEmployee);

        LeaveRequest leave1 = new LeaveRequest();
        leave1.setStartDate(LocalDate.of(2026, 10, 1));
        leave1.setEndDate(LocalDate.of(2026, 10, 3));
        leave1.setReason("Vacation");
        leave1.setStatus(LeaveStatus.PENDING);
        leave1.setEmployee(employee1);

        LeaveRequest leave2 = new LeaveRequest();
        leave2.setStartDate(LocalDate.of(2026, 11, 1));
        leave2.setEndDate(LocalDate.of(2026, 11, 2));
        leave2.setReason("Personal");
        leave2.setStatus(LeaveStatus.PENDING);
        leave2.setEmployee(employee2);

        LeaveRequest leave3 = new LeaveRequest();
        leave3.setStartDate(LocalDate.of(2026, 12, 1));
        leave3.setEndDate(LocalDate.of(2026, 12, 2));
        leave3.setReason("Other");
        leave3.setStatus(LeaveStatus.PENDING);
        leave3.setEmployee(otherEmployee);

        entityManager.persistAndFlush(leave1);
        entityManager.persistAndFlush(leave2);
        entityManager.persistAndFlush(leave3);

        List<LeaveRequest> result =
                leaveRequestRepository.findByEmployeeManagerId(manager.getId());

        assertEquals(2, result.size());
    }

    @Test
    void shouldDetectOverlappingLeave() {

        Employee employee = new Employee();
        employee.setName("Employee");
        employee.setEmail("employee@test.com");

        entityManager.persist(employee);

        LeaveRequest leave = new LeaveRequest();
        leave.setStartDate(LocalDate.of(2026, 10, 10));
        leave.setEndDate(LocalDate.of(2026, 10, 12));
        leave.setReason("Vacation");
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setEmployee(employee);

        entityManager.persistAndFlush(leave);

        List<LeaveStatus> blockingStatuses =
                List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING);

        // Overlaps existing leave: Oct 11 - Oct 15
        boolean overlap =
                leaveRequestRepository
                        .existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employee.getId(),
                                blockingStatuses,
                                LocalDate.of(2026, 10, 15),
                                LocalDate.of(2026, 10, 11)
                        );

        assertEquals(true, overlap);

        // Does not overlap: Oct 13 - Oct 15
        boolean noOverlap =
                leaveRequestRepository
                        .existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employee.getId(),
                                blockingStatuses,
                                LocalDate.of(2026, 10, 15),
                                LocalDate.of(2026, 10, 13)
                        );

        assertEquals(false, noOverlap);
    }

    @Test
    void shouldPaginateLeaveRequestsByEmployeeId() {

        Employee employee = new Employee();
        employee.setName("Employee");
        employee.setEmail("employee@test.com");

        entityManager.persist(employee);

        LeaveRequest leave1 = new LeaveRequest();
        leave1.setStartDate(LocalDate.of(2026, 10, 1));
        leave1.setEndDate(LocalDate.of(2026, 10, 2));
        leave1.setReason("Vacation");
        leave1.setStatus(LeaveStatus.PENDING);
        leave1.setEmployee(employee);

        LeaveRequest leave2 = new LeaveRequest();
        leave2.setStartDate(LocalDate.of(2026, 11, 1));
        leave2.setEndDate(LocalDate.of(2026, 11, 2));
        leave2.setReason("Personal");
        leave2.setStatus(LeaveStatus.PENDING);
        leave2.setEmployee(employee);

        entityManager.persistAndFlush(leave1);
        entityManager.persistAndFlush(leave2);

        Page<LeaveRequest> result =
                leaveRequestRepository.findByEmployeeId(
                        employee.getId(),
                        PageRequest.of(0, 1)
                );

        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getSize());
    }
}
