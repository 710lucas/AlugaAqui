package com._lucas.alugaqui.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para atualização de informações do usuário.")
public class UsuarioUpdateDTO {

    @Schema(description = "Novo nome do usuário.")
    private String nome;

    @Schema(description = "Novo email do usuário.")
    private String email;

    @Size(max = 13)
    @Schema(description = "Novo telefone do usuário. Máximo de 13 caracteres (ex: 5500912345678).")
    private String telefone;

    private String senha;

    public UsuarioUpdateDTO(String nome, String email, String telefone, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsuarioUpdateDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}