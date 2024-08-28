package com.mindex.challenge.dao;

import com.mindex.challenge.model.Compensation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {

    // This would be an inexpensive and quick query since most employees would have a relatively small compensation record history
    Optional<Compensation> findTopByEmployeeIdOrderByEffectiveDateDesc(String employeeId);
}
