package br.edu.atitus.controller;

import br.edu.atitus.model.FavoriteEntity;
import br.edu.atitus.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/customer-service/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{vehicleId}")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable UUID vehicleId,
            @RequestHeader("X-User-Id") String userIdStr) {

        UUID userId = UUID.fromString(userIdStr);
        String message = favoriteService.toggleFavorite(userId, vehicleId);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteEntity>> getMyFavorites(
            @RequestHeader("X-User-Id") String userIdStr) {

        UUID userId = UUID.fromString(userIdStr);
        List<FavoriteEntity> favorites = favoriteService.getFavoritesByUser(userId);
        return ResponseEntity.ok(favorites);
    }
}