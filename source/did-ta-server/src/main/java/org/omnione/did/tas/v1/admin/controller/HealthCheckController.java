/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
