package com.project.agriculture.service;

import com.project.agriculture.dto.IrrigationScheduleRequest;
import com.project.agriculture.dto.IrrigationScheduleResponse;
import com.project.agriculture.entity.Crop;
import com.project.agriculture.entity.FarmerProfile;
import com.project.agriculture.entity.IrrigationSchedule;
import com.project.agriculture.entity.IrrigationStatus;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.CropRepository;
import com.project.agriculture.repository.IrrigationScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IrrigationScheduleService {

    // Sensible defaults if the farmer doesn't specify these — tweak to taste
    private static final int DEFAULT_INTERVAL_DAYS = 4;
    private static final int DEFAULT_DURATION_DAYS = 30;

    private final IrrigationScheduleRepository irrigationScheduleRepository;
    private final CropRepository cropRepository;

    @Transactional
    public List<IrrigationScheduleResponse> generateSchedule(FarmerProfile farmer, IrrigationScheduleRequest request) {
        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + request.getCropId()));

        if (farmer.getLandSize() == null) {
            throw new IllegalStateException(
                    "Your farmer profile is missing a land size. Update your profile before generating a schedule.");
        }

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        int intervalDays = request.getIntervalDays() != null ? request.getIntervalDays() : DEFAULT_INTERVAL_DAYS;
        int durationDays = request.getDurationDays() != null ? request.getDurationDays() : DEFAULT_DURATION_DAYS;

        // Clear any un-actioned entries for this exact farmer+crop before regenerating,
        // so calling this twice doesn't leave duplicate PENDING rows behind.
        // Anything already marked COMPLETED or SKIPPED is left alone — that's real history.
        List<IrrigationSchedule> existingPending = irrigationScheduleRepository
                .findByFarmerIdAndStatus(farmer.getId(), IrrigationStatus.PENDING)
                .stream()
                .filter(entry -> entry.getCrop().getId().equals(crop.getId()))
                .toList();
        irrigationScheduleRepository.deleteAll(existingPending);

        List<IrrigationSchedule> generated = buildScheduleEntries(farmer, crop, startDate, intervalDays, durationDays);
        List<IrrigationSchedule> saved = irrigationScheduleRepository.saveAll(generated);

        return saved.stream().map(this::toResponse).toList();
    }

    private List<IrrigationSchedule> buildScheduleEntries(
            FarmerProfile farmer, Crop crop, LocalDate startDate, int intervalDays, int durationDays) {

        int numberOfSessions = Math.max(1, durationDays / intervalDays);

        // Treat crop.minWater/maxWater as the water requirement PER ACRE for the
        // whole growing period; scale by the farmer's land size to get a total,
        // then split that total evenly across every scheduled session.
        BigDecimal avgWaterPerAcre = crop.getMinWater()
                .add(crop.getMaxWater())
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        BigDecimal totalWaterNeeded = avgWaterPerAcre.multiply(farmer.getLandSize());

        BigDecimal waterPerSession = totalWaterNeeded
                .divide(BigDecimal.valueOf(numberOfSessions), 2, RoundingMode.HALF_UP);

        List<IrrigationSchedule> entries = new ArrayList<>();
        for (int i = 0; i < numberOfSessions; i++) {
            IrrigationSchedule entry = new IrrigationSchedule();
            entry.setFarmer(farmer);
            entry.setCrop(crop);
            entry.setScheduleDate(startDate.plusDays((long) i * intervalDays));
            entry.setWaterAmount(waterPerSession);
            entry.setStatus(IrrigationStatus.PENDING);
            entries.add(entry);
        }
        return entries;
    }

    @Transactional(readOnly = true)
    public List<IrrigationScheduleResponse> getMySchedule(FarmerProfile farmer) {
        return irrigationScheduleRepository.findByFarmerId(farmer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IrrigationScheduleResponse> getTodayTasks(FarmerProfile farmer) {
        return irrigationScheduleRepository
                .findByScheduleDateAndStatus(LocalDate.now(), IrrigationStatus.PENDING)
                .stream()
                .filter(entry -> entry.getFarmer().getId().equals(farmer.getId()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public IrrigationScheduleResponse updateStatus(FarmerProfile farmer, Long scheduleId, IrrigationStatus status) {
        IrrigationSchedule entry = irrigationScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Irrigation schedule entry not found with id: " + scheduleId));

        // Ownership check — a farmer must never be able to update someone else's schedule,
        // even if they guess a valid id.
        if (!entry.getFarmer().getId().equals(farmer.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this schedule entry");
        }

        entry.setStatus(status);
        IrrigationSchedule updated = irrigationScheduleRepository.save(entry);
        return toResponse(updated);
    }

    private IrrigationScheduleResponse toResponse(IrrigationSchedule entry) {
        return new IrrigationScheduleResponse(
                entry.getId(),
                entry.getCrop().getId(),
                entry.getCrop().getName(),
                entry.getScheduleDate(),
                entry.getWaterAmount(),
                entry.getStatus()
        );
    }
}
