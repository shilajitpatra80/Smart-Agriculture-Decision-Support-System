package com.project.agriculture.service;

import com.project.agriculture.dto.FertilizerRequest;
import com.project.agriculture.dto.FertilizerResponse;
import com.project.agriculture.entity.Crop;
import com.project.agriculture.entity.Fertilizer;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.CropRepository;
import com.project.agriculture.repository.FertilizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FertilizerService {

    private final FertilizerRepository fertilizerRepository;
    private final CropRepository cropRepository; // needed to validate/attach the parent crop

    public List<FertilizerResponse> getAllFertilizers() {
        return fertilizerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FertilizerResponse getFertilizerById(Long id) {
        return toResponse(findFertilizerOrThrow(id));
    }

    // Used by the farmer-facing "GET /api/fertilizer/{cropId}" endpoint later too
    public List<FertilizerResponse> getFertilizersByCropId(Long cropId) {
        return fertilizerRepository.findByCropId(cropId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FertilizerResponse createFertilizer(FertilizerRequest request) {
        Crop crop = findCropOrThrow(request.getCropId());

        Fertilizer fertilizer = new Fertilizer();
        fertilizer.setCrop(crop);
        applyRequestToEntity(request, fertilizer, crop);

        Fertilizer saved = fertilizerRepository.save(fertilizer);
        return toResponse(saved);
    }

    @Transactional
    public FertilizerResponse updateFertilizer(Long id, FertilizerRequest request) {
        Fertilizer fertilizer = findFertilizerOrThrow(id);
        Crop crop = findCropOrThrow(request.getCropId());

        applyRequestToEntity(request, fertilizer, crop);

        Fertilizer updated = fertilizerRepository.save(fertilizer);
        return toResponse(updated);
    }

    @Transactional
    public void deleteFertilizer(Long id) {
        Fertilizer fertilizer = findFertilizerOrThrow(id);
        fertilizerRepository.delete(fertilizer);
    }

    // ---- helpers ----

    private Fertilizer findFertilizerOrThrow(Long id) {
        return fertilizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fertilizer not found with id: " + id));
    }

    private Crop findCropOrThrow(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + cropId));
    }

    private void applyRequestToEntity(FertilizerRequest request, Fertilizer fertilizer, Crop crop) {
        fertilizer.setCrop(crop);
        fertilizer.setName(request.getName());
        fertilizer.setQuantityPerAcre(request.getQuantityPerAcre());
        fertilizer.setStageOfApplication(request.getStageOfApplication());
    }

    private FertilizerResponse toResponse(Fertilizer fertilizer) {
        return new FertilizerResponse(
                fertilizer.getId(),
                fertilizer.getCrop().getId(),
                fertilizer.getCrop().getName(),
                fertilizer.getName(),
                fertilizer.getQuantityPerAcre(),
                fertilizer.getStageOfApplication(),
                fertilizer.getCreatedAt()
        );
    }
}
