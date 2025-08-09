package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.dtos.CreateUserDTO;
import com.empresa.contabilidade.alvara_monitor.dtos.EditUserDTO;
import com.empresa.contabilidade.alvara_monitor.dtos.UserResponseDTO;
import com.empresa.contabilidade.alvara_monitor.exceptions.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exceptions.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.entities.User;
import com.empresa.contabilidade.alvara_monitor.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public UserResponseDTO save(final CreateUserDTO data) {
        if (Objects.nonNull(userRepository.findByLogin(data.login()))) {
            throw new BusinessException("Login já existente.");
        }

        final var encryptedPassword = passwordEncoder.encode(data.password());

        final var newUser = new User(
                data.login(),
                encryptedPassword,
                data.role()
        );

        final var savedUser = userRepository.save(newUser);
        return toDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO update(final Long id, final EditUserDTO data) {
        final var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        final var newLoginUser = userRepository.findByLogin(data.login());
        if (Objects.nonNull(newLoginUser) && !newLoginUser.getId().equals(id)) {
            throw new BusinessException("Login já está em uso por outro usuário.");
        }

        user.setLogin(data.login());
        user.setRole(data.role());
        return toDTO(user);
    }

    @Transactional
    public void delete(final Long id) {
        final var user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        userRepository.delete(user);
    }

    private UserResponseDTO toDTO(final User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getAuthorities().iterator().next().getAuthority()
        );
    }
}