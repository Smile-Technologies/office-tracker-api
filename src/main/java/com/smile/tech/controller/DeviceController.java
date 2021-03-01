package com.smile.tech.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smile.tech.payload.request.DeviceCreateRequest;
import com.smile.tech.service.DeviceService;

import io.swagger.annotations.Api;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/dev")
@Api
public class DeviceController {

    private static Logger logger = LoggerFactory.getLogger(DeviceController.class);
	
	@Autowired
	DeviceService DService;
	
	@PostMapping("/create")
    public ResponseEntity<?> createDevice(@Valid @RequestBody DeviceCreateRequest deviceCreateRequest){
		
		logger.info("Started Device Creation.");
		return DService.createDeviceRequestProcess(deviceCreateRequest);
	}
}
