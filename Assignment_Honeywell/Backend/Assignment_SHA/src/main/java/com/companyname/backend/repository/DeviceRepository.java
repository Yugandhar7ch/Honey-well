package com.companyname.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.companyname.backend.entity.Device;


/**
 * Repository interface for managing Device entities.
 * 
 * This interface extends JpaRepository, providing CRUD (Create, Read, Update, Delete)
 * functionality out-of-the-box for the Device entity. It inherits basic database operations
 * such as saving, updating, deleting, and querying devices.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
}