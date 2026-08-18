package com.erp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.dto.UserRequest;
import com.erp.entity.User;
import com.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(UserRequest request) {
        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User update(Long id, UserRequest request) {
        User existing = getById(id);
        existing.setFullName(request.getFullName());
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setPassword(request.getPassword());
        existing.setPhone(request.getPhone());
        existing.setRole(request.getRole());
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        return userRepository.save(existing);
    }

    public String delete(Long id) {
        User user = getById(id);
        userRepository.delete(user);
        return "User deleted successfully with id : " + id;
    }
}