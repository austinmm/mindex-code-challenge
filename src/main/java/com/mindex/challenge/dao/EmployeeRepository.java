package com.mindex.challenge.dao;

import com.mindex.challenge.data.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Optional<Employee> findByEmployeeId(String employeeId);
}
