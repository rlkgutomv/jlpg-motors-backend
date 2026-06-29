package br.edu.atitus.service;

import br.edu.atitus.model.VehicleEntity;
import br.edu.atitus.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleEntity> findAll() {
        return vehicleRepository.findAll();
    }

    public Optional<VehicleEntity> findById(UUID id) {
        return vehicleRepository.findById(id);
    }

    public VehicleEntity save(VehicleEntity vehicle) {
        if (vehicle.getPlate() == null || vehicle.getPlate().trim().isEmpty()) {
            throw new RuntimeException("A placa do veículo é obrigatória!");
        }

        String cleanPlate = vehicle.getPlate().trim().toUpperCase();
        vehicle.setPlate(cleanPlate);

        if (vehicle.getId() == null) {
            if (vehicleRepository.findByPlate(cleanPlate).isPresent()) {
                throw new RuntimeException("Já existe um veículo cadastrado com esta placa!");
            }
        }

        validateVehicleFields(vehicle);
        return vehicleRepository.save(vehicle);
    }

    public VehicleEntity update(UUID id, VehicleEntity updatedVehicle) {
        VehicleEntity existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado!"));

        String cleanPlate = updatedVehicle.getPlate().trim().toUpperCase();

        if (!existingVehicle.getPlate().equalsIgnoreCase(cleanPlate)) {
            if (vehicleRepository.findByPlate(cleanPlate).isPresent()) {
                throw new RuntimeException("Já existe um veículo cadastrado com esta placa!");
            }
        }

        existingVehicle.setName(updatedVehicle.getName());
        existingVehicle.setBrand(updatedVehicle.getBrand());
        existingVehicle.setModel(updatedVehicle.getModel());
        existingVehicle.setYearModel(updatedVehicle.getYearModel());
        existingVehicle.setPrice(updatedVehicle.getPrice());
        existingVehicle.setMileage(updatedVehicle.getMileage());
        existingVehicle.setTransmission(updatedVehicle.getTransmission());
        existingVehicle.setFuelType(updatedVehicle.getFuelType());
        existingVehicle.setCategory(updatedVehicle.getCategory());
        existingVehicle.setColor(updatedVehicle.getColor());
        existingVehicle.setStock(updatedVehicle.getStock());
        existingVehicle.setDescription(updatedVehicle.getDescription());
        existingVehicle.setImageUrl(updatedVehicle.getImageUrl());
        existingVehicle.setPlate(cleanPlate);

        validateVehicleFields(existingVehicle);
        return vehicleRepository.save(existingVehicle);
    }

    public void delete(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado para exclusão!");
        }
        vehicleRepository.deleteById(id);
    }

    private void validateVehicleFields(VehicleEntity vehicle) {
        if (vehicle.getName() == null || vehicle.getName().trim().isEmpty()) {
            throw new RuntimeException("O nome do veículo é obrigatório!");
        }
        if (vehicle.getBrand() == null || vehicle.getBrand().trim().isEmpty()) {
            throw new RuntimeException("A marca do veículo é obrigatória!");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new RuntimeException("O modelo do veículo é obrigatório!");
        }
        if (vehicle.getYearModel() < 1900) {
            throw new RuntimeException("O ano do veículo deve ser maior ou igual a 1900!");
        }
        if (vehicle.getPrice() == null || vehicle.getPrice().doubleValue() <= 0) {
            throw new RuntimeException("O preço do veículo deve ser maior que zero!");
        }
        if (vehicle.getTransmission() == null || vehicle.getTransmission().trim().isEmpty()) {
            throw new RuntimeException("O tipo de câmbio é obrigatório!");
        }
        if (vehicle.getFuelType() == null || vehicle.getFuelType().trim().isEmpty()) {
            throw new RuntimeException("O tipo de combustível é obrigatório!");
        }
        if (vehicle.getCategory() == null || vehicle.getCategory().trim().isEmpty()) {
            throw new RuntimeException("A categoria é obrigatória!");
        }
        if (vehicle.getColor() == null || vehicle.getColor().trim().isEmpty()) {
            throw new RuntimeException("A cor é obrigatória!");
        }
        if (vehicle.getStock() < 0) {
            throw new RuntimeException("A quantidade em estoque não pode ser negativa!");
        }
    }
}