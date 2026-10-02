package com.project.agriculture.controller;

import com.project.agriculture.dto.IrrigationScheduleRequest;
import com.project.agriculture.dto.IrrigationScheduleResponse;
import com.project.agriculture.dto.IrrigationStatusUpdateRequest;
import com.project.agriculture.entity.FarmerProfile;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.FarmerProfileRepository;
import com.project.agriculture.security.UserPrincipal;
import com.project.agriculture.service.IrrigationScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irrigation")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')") // every endpoint here is a farmer managing their own schedule
public class IrrigationScheduleController {

    private final IrrigationScheduleService irrigationScheduleService;
    private final FarmerProfileRepository farmerProfileRepository;

    // POST /api/irrigation/schedule
    @PostMapping("/schedule")
    public ResponseEntity<List<IrrigationScheduleResponse>> generateSchedule(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody IrrigationScheduleRequest request) {

        FarmerProfile farmer = findFarmerOrThrow(principal.getUser().getId());
        return ResponseEntity.ok(irrigationScheduleService.generateSchedule(farmer, request));
    }

    // GET /api/irrigation/my-schedule
    @GetMapping("/my-schedule")
    public ResponseEntity<List<IrrigationScheduleResponse>> getMySchedule(
            @AuthenticationPrincipal UserPrincipal principal) {

        FarmerProfile farmer = findFarmerOrThrow(principal.getUser().getId());
        return ResponseEntity.ok(irrigationScheduleService.getMySchedule(farmer));
    }

    // GET /api/irrigation/today — handy for a "today's tasks" dashboard widget
    @GetMapping("/today")
    public ResponseEntity<List<IrrigationScheduleResponse>> getTodayTasks(
            @AuthenticationPrincipal UserPrincipal principal) {

        FarmerProfile farmer = findFarmerOrThrow(principal.getUser().getId());
        return ResponseEntity.ok(irrigationScheduleService.getTodayTasks(farmer));
    }

    // PATCH /api/irrigation/5/status — farmer marks a task done/skipped
    @PatchMapping("/{id}/status")
    public ResponseEntity<IrrigationScheduleResponse> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody IrrigationStatusUpdateRequest request) {

        FarmerProfile farmer = findFarmerOrThrow(principal.getUser().getId());
        return ResponseEntity.ok(irrigationScheduleService.updateStatus(farmer, id, request.getStatus()));
    }

    private FarmerProfile findFarmerOrThrow(Long userId) {
        return farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No farmer profile found. Complete your profile first: POST /api/farmer/profile"));
    }
}
