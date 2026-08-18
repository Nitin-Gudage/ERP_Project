package com.erp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.dto.HistoryRequest;
import com.erp.entity.History;
import com.erp.entity.HistoryAction;
import com.erp.entity.User;
import com.erp.repository.HistoryRepository;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public History create(HistoryRequest request) {
        User user = null;
        if (request.getPerformedBy() != null) {
            user = userRepository.findById(request.getPerformedBy())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getPerformedBy()));
        }

        History history = History.builder()
                .action(request.getAction())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType().trim().toUpperCase())
                .performedBy(user)
                .description(request.getDescription())
                .build();
        return historyRepository.save(history);
    }

    public History createHistory(HistoryAction action, String referenceType, Long referenceId, Long userId, String description) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        History history = History.builder()
                .action(action)
                .referenceType(referenceType.trim().toUpperCase())
                .referenceId(referenceId)
                .performedBy(user)
                .description(description)
                .build();
        return historyRepository.save(history);
    }

    public List<History> getAll() {
        return historyRepository.findAll();
    }

    public History getById(Long id) {
        return historyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("History not found with id: " + id));
    }

    public List<History> getByReference(String referenceType, Long referenceId) {
        return historyRepository.findByReferenceTypeAndReferenceId(referenceType.trim().toUpperCase(), referenceId);
    }

    public List<History> getByAction(String action) {
        HistoryAction historyAction;
        try {
            historyAction = HistoryAction.valueOf(action.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid history action: " + action);
        }
        return historyRepository.findByAction(historyAction);
    }

    public List<History> getByUser(Long userId) {
        return historyRepository.findByPerformedById(userId);
    }
}