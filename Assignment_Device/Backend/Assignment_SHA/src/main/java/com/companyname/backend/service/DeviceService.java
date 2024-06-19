package com.companyname.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.companyname.backend.entity.Device;
import com.companyname.backend.repository.DeviceRepository;

@Service
public class DeviceService {
	 @Autowired
	    private DeviceRepository deviceRepository;

	 /**
	  * Service method to save a Device object to the database.
	  * 
	  * This method takes a Device object as input and delegates the persistence
	  * operation to the DeviceRepository. It returns the saved Device object.
	  * 
	  * @param device the Device object to be saved to the database
	  * @return the saved Device object with any automatically generated fields (e.g., ID)
	  */
	    public Device saveDevice(Device device) {
	        return deviceRepository.save(device);
	    }
	    
	    public List<Device> getAllDevices() {
	        return deviceRepository.findAll();
	    }
	    
	    public Device getDeviceById(Long id) {
	        return deviceRepository.findById(id).orElse(null);
	    }
}
