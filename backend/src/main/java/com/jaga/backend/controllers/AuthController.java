package com.jaga.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaga.backend.data.dto.LoginSuccessDto;
import com.jaga.backend.data.dto.RegisterDto;
import com.jaga.backend.data.entity.User;
import com.jaga.backend.data.service.JwtService;
import com.jaga.backend.data.service.MessageSendingService;
import com.jaga.backend.data.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@Controller
@AllArgsConstructor
@RestController
@MessageMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final MessageSendingService messageSendingService;

    @MessageMapping("/register")
    @SendTo("/topic/auth")
    @Transactional
    public String registerUser(@Payload RegisterDto registerDto) throws Exception {

        char[] password = registerDto.password().toCharArray();
        User user = userService.registerUser(new User(registerDto.username(), password, new HashSet<>(), false));

        userService.registerUser(user);

        System.out.println("User registered: " + user.toString());

        return jwtService.createToken(user.getUsername());

    }

    @MessageMapping("/login")
    @SendTo("/topic/auth")
    @Transactional
    public void loginUser(@Payload RegisterDto registerDto) throws Exception {
        char[] password = registerDto.password().toCharArray();

        User user = userService.loginUser(registerDto.username(), password);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        System.out.println("User logged in: " + user.toString());

        /*var mapper = new ObjectMapper();

        messagingTemplate.convertAndSend(errorDto.destination(), mapper.writeValueAsString(errorDto));*/

        var loginSuccessDto = new LoginSuccessDto(jwtService.createToken(user.getUsername()), user.getUsername(), user.isAdmin(), "/topic/auth", HttpStatus.OK.value());

        //TODO change to message sending service
        System.out.println("Token: " + loginSuccessDto.token());
        messageSendingService.sendMessage(loginSuccessDto);
    }



}
