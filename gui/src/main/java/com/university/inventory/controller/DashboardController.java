package com.university.inventory.controller;

import com.university.inventory.dto.DashboardResponse;
import com.university.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the Dashboard endpoint.
 *
 * Provides a single endpoint that returns a summary of inventory statistics:
 * - Total products
 * - Total categories
 * - Low stock products and count
 *
 * Useful for building a dashboard view in a frontend application.
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /dashboard
     * Returns aggregated inventory statistics.
     * HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
