package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Usuario.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para criação de um novo usuário no sistema.")
public class UsuarioCreateDTO {

    @NotBlank
    @Schema(description = "Nome completo do usuário.")
    private String nome;

    @NotBlank
    @Schema(description = "Email do usuário (usado como nome de usuário para login).")
    private String email;

    @NotBlank
    @Size(max = 13)
    @Schema(description = "Telefone do usuário. Máximo de 13 caracteres (ex: 5500912345678).")
    private String telefone;

    @NotNull
    @Schema(description = "Papel do usuário (LOCATARIO ou LOCADOR).")
    private Role role;

    @NotBlank
    @Schema(description = "Senha para o login.")
    private String senha;

    public UsuarioCreateDTO(String nome, String email, String telefone, Role role, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.senha = senha;
    }

    public UsuarioCreateDTO() {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}