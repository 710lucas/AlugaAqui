package com._lucas.alugaqui.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para requisição de login (autenticação).")
public class LoginRequestDTO {

    @Schema(description = "Email do usuário (username).")
    private String username;

    @Schema(description = "Senha do usuário.")
    private String password;

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}