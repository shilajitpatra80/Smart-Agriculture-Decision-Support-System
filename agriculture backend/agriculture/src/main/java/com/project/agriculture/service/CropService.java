package com.project.agriculture.service;

import com.project.agriculture.dto.CropRequest;
import com.project.agriculture.dto.CropResponse;
import com.project.agriculture.entity.Crop;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.CropRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok generates a constructor for the final field below (constructor injection)
public class CropService {

    private final CropRepository cropRepository;

    public List<CropResponse> getAllCrops() {
        return cropRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CropResponse getCropById(Long id) {
        Crop crop = findCropOrThrow(id);
        return toResponse(crop);
    }

    @Transactional
    public CropResponse createCrop(CropRequest request) {
        Crop crop = new Crop();
        applyRequestToEntity(request, crop);
        Crop saved = cropRepository.save(crop);
        return toResponse(saved);
    }

    @Transactional
    public CropResponse updateCrop(Long id, CropRequest request) {
        Crop crop = findCropOrThrow(id);
        applyRequestToEntity(request, crop);
        Crop updated = cropRepository.save(crop);
        return toResponse(updated);
    }

    @Transactional
    public void deleteCrop(Long id) {
        Crop crop = findCropOrThrow(id);
        cropRepository.delete(crop); // cascades to fertilizer/disease rows via FK ON DELETE CASCADE
    }

    // ---- helpers ----

    private Crop findCropOrThrow(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + id));
    }

    private void applyRequestToEntity(CropRequest request, Crop crop) {
        crop.setName(request.getName());
        crop.setSeason(request.getSeason());
        crop.setSoilType(request.getSoilType());
        crop.setMinWater(request.getMinWater());
        crop.setMaxWater(request.getMaxWater());
        crop.setIdealTempRange(request.getIdealTempRange());
    }

    private CropResponse toResponse(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getName(),
                crop.getSeason(),
                crop.getSoilType(),
                crop.getMinWater(),
                crop.getMaxWater(),
                crop.getIdealTempRange(),
                crop.getCreatedAt(),
                crop.getUpdatedAt()
        );
    }
}
