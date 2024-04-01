package com.jaga.backend.data.service;

import com.jaga.backend.data.dto.ErrorDto;
import com.jaga.backend.data.entity.User;
import com.jaga.backend.data.repositories.UserRepository;
import com.jaga.backend.error.ExceptionHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MessageSendingService messageSendingService;
    // todo private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) throws Exception{
        if (getUserByUsername(user.getUsername()).isPresent()) {
            messageSendingService.sendError(new ErrorDto("User already exists", "/topic/auth", HttpStatus.BAD_REQUEST));

        }

        return userRepository.save(user);
    }


    public User loginUser(String username, char[] password) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!Arrays.toString(user.getPassword()).equals(Arrays.toString(password))) {
            messageSendingService.sendError(new ErrorDto("Incorrect password", "/topic/auth", HttpStatus.BAD_REQUEST));
        }

        return user;
    }

    

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

        public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
        }

    public User updateUser(Long id, User userUpdate) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userUpdate.getUsername());
                    user.setPassword(userUpdate.getPassword());
                    user.setChats(userUpdate.getChats());
                    user.setAdmin(userUpdate.isAdmin());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
