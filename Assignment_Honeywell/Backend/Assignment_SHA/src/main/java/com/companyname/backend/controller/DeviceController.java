package com.companyname.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.companyname.backend.entity.Device;
import com.companyname.backend.service.DeviceService;

import org.springframework.http.ResponseEntity;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
	@Autowired
	private DeviceService deviceService;

	/**
	 * REST controller endpoint to handle POST requests for saving a device.
	 * 
	 * This method accepts a JSON request body that is mapped to a Device object. It
	 * delegates the saving of the device to the DeviceService, and then returns the
	 * saved device object wrapped in a ResponseEntity.
	 * 
	 * @param Gets the Device object from the incoming JSON request body
	 * @return ResponseEntity containing the saved Device object and an HTTP status
	 *         code of 200 (OK)
	 */
	@PostMapping
	public ResponseEntity<Device> saveDevice(@RequestBody Device device) {
		Device savedDevice = deviceService.saveDevice(device);
		return ResponseEntity.ok(savedDevice);
	}

	/**
	 * Endpoint to generate signatures for all devices.
	 * 
	 * @return A ResponseEntity containing a list of base64-encoded signatures of
	 *         JSON representations of devices. If no devices are found, returns a
	 *         404 Not Found status. In case of any other errors, returns a 500
	 *         Internal Server Error status with an empty list.
	 */
	@GetMapping("/signatures")
	public ResponseEntity<List<String>> generateSignaturesForAllDevices() {
		// Retrieve all devices from the database
		List<Device> devices = deviceService.getAllDevices();
		if (devices.isEmpty()) {
			// Return 404 Not Found if no devices are found
			return ResponseEntity.notFound().build();
		}

		try {
			// Get the private key for signing
			PrivateKey privateKey = getPrivateKey();

			Signature signature = Signature.getInstance("SHA256withRSA");

			// List to store generated signatures
			List<String> signatures = new ArrayList<>();

			// Iterate over each device and generate the signature
			for (Device device : devices) {

				// Create JSON representation of the device
				String jsonData = "{\"serialNumber\":\"" + device.getSerialNumber() + "\"," + "\"portNumber\":\""
						+ device.getPortNumber() + "\"}";

				// Sign the JSON data
				signature.initSign(privateKey);
				signature.update(jsonData.getBytes());
				byte[] signedData = signature.sign();

				// Encode the signature in base64 and add to the list
				String signatureBase64 = Base64.getEncoder().encodeToString(signedData);
				signatures.add(signatureBase64);
			}
			// Return the list of signatures with 200 OK status
			return ResponseEntity.ok(signatures);
		} catch (Exception e) {
			// Log the error (logging can be added here)
			// Return 500 Internal Server Error with an empty list in case of any exceptions
			return ResponseEntity.status(500).body(new ArrayList<>()); // Empty list for signatures

		}
	}

	/**
	 * Method to retrieve the private key for signing.
	 * 
	 * @return The PrivateKey object.
	 * @throws Exception If an error occurs while retrieving the private key.
	 */
	private PrivateKey getPrivateKey() throws Exception {
		// example RSA private key in PEM format
		String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
				+ "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAM7t8Ub1DP+B91NJ\n"
				+ "nC45zqIvd1QXkQ5Ac1EJl8mUglWFzUyFbhjSuF4mEjrcecwERfRummASbLoyeMXl\n"
				+ "eiPg7jvSaz2szpuV+afoUo9c1T+ORNUzq31NvM7IW6+4KhtttwbMq4wbbPpBfVXA\n"
				+ "IAhvnLnCp/VyY/npkkjAid4c7RoVAgMBAAECgYBcCuy6kj+g20+G5YQp756g95oN\n"
				+ "dpoYC8T/c9PnXz6GCgkik2tAcWJ+xlJviihG/lObgSL7vtZMEC02YXdtxBxTBNmd\n"
				+ "upkruOkL0ElIu4S8CUwD6It8oNnHFGcIhwXUbdpSCr1cx62A0jDcMVgneQ8vv6vB\n"
				+ "/YKlj2dD2SBq3aaCYQJBAOvc5NDyfrdMYYTY+jJBaj82JLtQ/6K1vFIwdxM0siRF\n"
				+ "UYqSRA7G8A4ga+GobTewgeN6URFwWKvWY8EGb3HTwFkCQQDgmKtjjJlX3BotgnGD\n"
				+ "gdxVgvfYG39BL2GnotSwUbjjce/yZBtrbcClfqrrOWWw7lPcX1d0v8o3hJfLF5dT\n"
				+ "6NAdAkA8qAQYUCSSUwxJM9u0DOqb8vqjSYNUftQ9dsVIpSai+UitEEx8WGDn4SKd\n"
				+ "V8kupy/gJlau22uSVYI148fJSCGRAkBz+GEHFiJX657YwPI8JWHQBcBUJl6fGggi\n"
				+ "t0F7ibceOkbbsjU2U4WV7sHyk8Cei3Fh6RkPf7i60gxPIe9RtHVBAkAnPQD+BmND\n"
				+ "By8q5f0Kwtxgo2+YkxGDP5bxDV6P1vd2C7U5/XxaN53Kc0G8zu9UlcwhZcQ5BljH\n" + "N24cUWZOo+60\n"
				+ "-----END PRIVATE KEY-----";

		// Clean the PEM string: remove header, footer, and any whitespace
		String privateKeyContent = privateKeyPem.replaceAll("-----BEGIN PRIVATE KEY-----", "")
				.replaceAll("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");

		// Decode the base64 encoded string
		byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);

		// Generate the private key
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(spec);
	}

}
