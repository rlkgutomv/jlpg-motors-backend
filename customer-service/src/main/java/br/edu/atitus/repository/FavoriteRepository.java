package br.edu.atitus.repository;

import br.edu.atitus.model.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {
    Optional<FavoriteEntity> findByUserIdAndVehicleId(UUID userId, UUID vehicleId);
    List<FavoriteEntity> findByUserId(UUID userId);
}