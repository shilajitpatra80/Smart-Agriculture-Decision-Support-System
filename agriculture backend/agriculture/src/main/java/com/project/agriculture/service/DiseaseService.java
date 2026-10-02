package com.project.agriculture.service;

import com.project.agriculture.dto.DiseaseRequest;
import com.project.agriculture.dto.DiseaseResponse;
import com.project.agriculture.entity.Crop;
import com.project.agriculture.entity.Disease;
import com.project.agriculture.exception.ResourceNotFoundException;
import com.project.agriculture.repository.CropRepository;
import com.project.agriculture.repository.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final CropRepository cropRepository;

    public List<DiseaseResponse> getAllDiseases() {
        return diseaseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DiseaseResponse getDiseaseById(Long id) {
        return toResponse(findDiseaseOrThrow(id));
    }

    // Used by the farmer-facing "GET /api/disease/{cropId}" endpoint later too
    public List<DiseaseResponse> getDiseasesByCropId(Long cropId) {
        return diseaseRepository.findByCropId(cropId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DiseaseResponse createDisease(DiseaseRequest request) {
        Crop crop = findCropOrThrow(request.getCropId());

        Disease disease = new Disease();
        applyRequestToEntity(request, disease, crop);

        Disease saved = diseaseRepository.save(disease);
        return toResponse(saved);
    }

    @Transactional
    public DiseaseResponse updateDisease(Long id, DiseaseRequest request) {
        Disease disease = findDiseaseOrThrow(id);
        Crop crop = findCropOrThrow(request.getCropId());

        applyRequestToEntity(request, disease, crop);

        Disease updated = diseaseRepository.save(disease);
        return toResponse(updated);
    }

    @Transactional
    public void deleteDisease(Long id) {
        Disease disease = findDiseaseOrThrow(id);
        diseaseRepository.delete(disease);
    }

    // ---- helpers ----

    private Disease findDiseaseOrThrow(Long id) {
        return diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with id: " + id));
    }

    private Crop findCropOrThrow(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + cropId));
    }

    private void applyRequestToEntity(DiseaseRequest request, Disease disease, Crop crop) {
        disease.setCrop(crop);
        disease.setName(request.getName());
        disease.setSymptoms(request.getSymptoms());
        disease.setTreatment(request.getTreatment());
    }

    private DiseaseResponse toResponse(Disease disease) {
        return new DiseaseResponse(
                disease.getId(),
                disease.getCrop().getId(),
                disease.getCrop().getName(),
                disease.getName(),
                disease.getSymptoms(),
                disease.getTreatment(),
                disease.getCreatedAt()
        );
    }
}
