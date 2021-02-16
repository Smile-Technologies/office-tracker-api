package com.smile.tech.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.smile.tech.model.Attendence;

public interface AttendenceRepository extends MongoRepository<Attendence, String> {

	public Attendence findByUserID(String userID);

}
