package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Usuario.Role;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioCreateDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @NotBlank
    private String telefone;

    @NotNull
    private Role role;

    @NotBlank
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
