package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.LoginRequestDTO;
import com._lucas.alugaqui.DTOs.TokenResponseDTO;
import com._lucas.alugaqui.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Endpoints para Login e Gerenciamento de Autenticação")
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

    @Operation(summary = "Realiza o login de um usuário e retorna um token JWT",
            description = "Autentica o usuário com email e senha e, em caso de sucesso, fornece um token Bearer para acesso às rotas protegidas.")
    @PostMapping("/login")
    public TokenResponseDTO createAuthenticationToken(@RequestBody LoginRequestDTO loginRequestDTO) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        String token = jwtUtil.generateToken(new HashMap<>(), userDetails.getUsername());
        return new TokenResponseDTO(token);

    }
}
