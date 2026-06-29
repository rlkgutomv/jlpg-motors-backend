package br.edu.atitus.service;

import br.edu.atitus.model.ChatNegotiationEntity;
import br.edu.atitus.repository.ChatNegotiationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatNegotiationService {

    private final ChatNegotiationRepository chatRepository;

    public ChatNegotiationService(ChatNegotiationRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public ChatNegotiationEntity startNegotiation(UUID userId, UUID vehicleId) {
        Optional<ChatNegotiationEntity> existing = chatRepository.findByUserIdAndVehicleIdAndStatus(userId, vehicleId, "OPEN");

        if (existing.isPresent()) {
            return existing.get(); // Retorna o chat existente se já estiver aberto
        }

        ChatNegotiationEntity newChat = new ChatNegotiationEntity(userId, vehicleId);
        return chatRepository.save(newChat);
    }

    public ChatNegotiationEntity closeNegotiation(UUID chatId) {
        ChatNegotiationEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Solicitação de negociação não encontrada!"));

        chat.setStatus("CLOSED");
        return chatRepository.save(chat);
    }

    public List<ChatNegotiationEntity> getMyChats(UUID userId) {
        return chatRepository.findByUserId(userId);
    }

    public List<ChatNegotiationEntity> getAllChatsForAdmin() {
        return chatRepository.findAll();
    }
}