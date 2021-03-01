package com.smile.tech.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smile.tech.model.Device;

public interface DeviceRepository extends MongoRepository<Device, String> {

	Optional<Device> findByDevicename(String devicename);

}
