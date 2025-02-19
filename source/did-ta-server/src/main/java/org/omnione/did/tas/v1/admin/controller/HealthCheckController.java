package org.omnione.did.tas.v1.admin.controller;

import org.omnione.did.base.constants.UrlConstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check API Controller
 *
 * This controller provides a basic health check endpoint.
 * It returns a JSON response indicating the server status.
 *
 * @author : yklee0911
 * @fileName : HealthCheckController
 * @since : 2/19/25
 */
@RestController
@RequestMapping(value = UrlConstant.Tas.ADMIN_V1)
public class HealthCheckController {

    /**
     * Health check endpoint (temporary)
     *
     * @return JSON response with health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Service is running");

        return ResponseEntity.ok(response);
    }
}
