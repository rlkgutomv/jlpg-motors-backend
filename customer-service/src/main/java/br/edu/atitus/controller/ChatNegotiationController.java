package br.edu.atitus.controller;

import br.edu.atitus.model.ChatNegotiationEntity;
import br.edu.atitus.service.ChatNegotiationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/customer-service/negotiations")
public class ChatNegotiationController {

    private final ChatNegotiationService chatService;

    public ChatNegotiationController(ChatNegotiationService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{vehicleId}")
    public ResponseEntity<ChatNegotiationEntity> startNegotiation(
            @PathVariable UUID vehicleId,
            @RequestHeader("X-User-Id") String userIdStr) {

        UUID userId = UUID.fromString(userIdStr);
        ChatNegotiationEntity chat = chatService.startNegotiation(userId, vehicleId);
        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chatId}/close")
    public ResponseEntity<ChatNegotiationEntity> closeNegotiation(
            @PathVariable UUID chatId) {

        ChatNegotiationEntity updatedChat = chatService.closeNegotiation(chatId);
        return ResponseEntity.ok(updatedChat);
    }

    @GetMapping("/my-chats")
    public ResponseEntity<List<ChatNegotiationEntity>> getMyChats(
            @RequestHeader("X-User-Id") String userIdStr) {

        UUID userId = UUID.fromString(userIdStr);
        List<ChatNegotiationEntity> chats = chatService.getMyChats(userId);
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<ChatNegotiationEntity>> getAllChats() {
        List<ChatNegotiationEntity> chats = chatService.getAllChatsForAdmin();
        return ResponseEntity.ok(chats);
    }
}