package br.edu.atitus.service;

import br.edu.atitus.model.FavoriteEntity;
import br.edu.atitus.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public String toggleFavorite(UUID userId, UUID vehicleId) {
        Optional<FavoriteEntity> existingFavorite = favoriteRepository.findByUserIdAndVehicleId(userId, vehicleId);

        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return "Veículo removido dos favoritos!";
        } else {
            FavoriteEntity favorite = new FavoriteEntity(userId, vehicleId);
            favoriteRepository.save(favorite);
            return "Veículo adicionado aos favoritos!";
        }
    }

    public List<FavoriteEntity> getFavoritesByUser(UUID userId) {
        return favoriteRepository.findByUserId(userId);
    }
}