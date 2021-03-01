package com.smile.tech.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.smile.tech.model.Device;
import com.smile.tech.payload.request.DeviceCreateRequest;
import com.smile.tech.payload.response.MessageResponse;
import com.smile.tech.repository.DeviceRepository;

@Service
public class DeviceService {

	private static Logger logger = LoggerFactory.getLogger(DeviceService.class);
	
	@Autowired
	DeviceRepository repository;
	
	public ResponseEntity<?> createDeviceRequestProcess(@Valid DeviceCreateRequest deviceCreateRequest) {
		Optional<Device> deviceExit=findByDevicename(deviceCreateRequest.getDevicename());
		if (deviceExit.isPresent()) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Device Name is already in use!"));
		}else {
			Device device=new Device();
			ZonedDateTime zdtAtET = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
	        LocalDateTime date = zdtAtET.toLocalDateTime();
			device.setDate(date);
			device.setDevicename(deviceCreateRequest.getDevicename());
			insert(device);
			logger.info("ended Device Creation.");
			return ResponseEntity.ok(new MessageResponse("New Device Created with name: "+deviceCreateRequest.getDevicename()+"!"));
		}
		
	}

	private Optional<Device> findByDevicename(String devicename) {
		return repository.findByDevicename(devicename);
	}
	
	private void insert(Device device) {
		repository.insert(device);
	}
	
}
