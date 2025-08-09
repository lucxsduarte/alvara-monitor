package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.dtos.NotificationSettingsDTO;
import com.empresa.contabilidade.alvara_monitor.dtos.CreateUserDTO;
import com.empresa.contabilidade.alvara_monitor.dtos.EditUserDTO;
import com.empresa.contabilidade.alvara_monitor.dtos.UserResponseDTO;
import com.empresa.contabilidade.alvara_monitor.services.NotificationSettingsService;
import com.empresa.contabilidade.alvara_monitor.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    private final NotificationSettingsService notificationSettingsService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> listUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/notifications/settings")
    public ResponseEntity<NotificationSettingsDTO> getNotificationSettings() {
        final var settings = notificationSettingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid final CreateUserDTO userDTO) {
        final var createdUser = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable final Long id, @RequestBody @Valid final EditUserDTO userDTO) {
        final var updatedUser = userService.update(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/notifications/settings")
    public ResponseEntity<NotificationSettingsDTO> updateNotificationSettings(@RequestBody final NotificationSettingsDTO dto) {
        final var updatedSettings = notificationSettingsService.updateSettings(dto);
        return ResponseEntity.ok(updatedSettings);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable final Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}