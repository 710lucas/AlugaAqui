package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.ChangePasswordDTO;
import com._lucas.alugaqui.DTOs.LoginRequestDTO;
import com._lucas.alugaqui.DTOs.TokenResponseDTO;
import com._lucas.alugaqui.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public TokenResponseDTO createAuthenticationToken(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        String token = jwtUtil.generateToken(new HashMap<>(), userDetails.getUsername());
        return new TokenResponseDTO(token);

    }


    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        return "ok";
    }


}
