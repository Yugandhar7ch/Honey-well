let savedDeviceId = null;

/**
 * Handles form submission for device data.
 * Retrieves serial number and port number from form inputs,
 * sends them as JSON data to the backend API endpoint for saving.
 * Displays a success message with the saved device ID or alerts
 * the user in case of an error.
 */
function submitForm() {
     // Retrieve values from form inputs
    const serialNumber = document.getElementById('serialNumber').value;
    const portNumber = document.getElementById('portNumber').value;

     // Prepare data object to send as JSON
    const data = {
        serialNumber: serialNumber,
        portNumber: portNumber
    };

     // Send POST request to backend API
    fetch('http://localhost:8080/api/devices', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json()) // Parse JSON response from the server
    .then(device => {
        // Handle successful response
        savedDeviceId = device.id;
        alert('Data saved as JSON. Device ID: ' + savedDeviceId);
    })
    .catch(error => {
        // Handle errors
        console.error('Error:', error);
        alert('Error saving data.');
    });
}

function downloadSignatures() {
    fetch('http://localhost:8080/api/devices/signatures')
    .then(response => response.json())
    .then(signatures => {
        // Process signatures here
        let signatureText = '';

        signatures.forEach(signature => {
            // Append each signature to the text content
            signatureText += signature + '\n';
        });

        // Create a Blob with the signature text
        const blob = new Blob([signatureText], { type: 'text/plain' });

        // Create a download link
        const downloadLink = document.createElement('a');
        downloadLink.href = URL.createObjectURL(blob);
        downloadLink.download = 'signatures.txt'; // File name

        // Append the download link to the body
        document.body.appendChild(downloadLink);

        // Programmatically trigger the download
        downloadLink.click();

        // Clean up
        document.body.removeChild(downloadLink);

        alert('Signatures downloaded successfully.');
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error downloading signatures.');
    });
}



