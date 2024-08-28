package com.mindex.challenge.dao;

import com.mindex.challenge.model.Compensation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {

    Optional<Compensation> findByEmployeeId(String employeeId);
}
