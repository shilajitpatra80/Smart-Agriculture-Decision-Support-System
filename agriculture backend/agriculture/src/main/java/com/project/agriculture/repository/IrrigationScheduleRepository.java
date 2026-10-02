package com.project.agriculture.repository;

import com.project.agriculture.entity.IrrigationSchedule;
import com.project.agriculture.entity.IrrigationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IrrigationScheduleRepository extends JpaRepository<IrrigationSchedule, Long> {

    // A farmer's full irrigation schedule (e.g. for a calendar view)
    List<IrrigationSchedule> findByFarmerId(Long farmerId);

    // Today's / upcoming pending tasks for a farmer's dashboard
    List<IrrigationSchedule> findByFarmerIdAndStatus(Long farmerId, IrrigationStatus status);

    // Useful for a scheduled job that checks what's due today
    List<IrrigationSchedule> findByScheduleDateAndStatus(LocalDate scheduleDate, IrrigationStatus status);
}
