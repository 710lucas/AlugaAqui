package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Usuario.Role;

public class UsuarioSimpleDTO {
    private Long id;
    private String nome;
    private String email;
    private Role role;

    public UsuarioSimpleDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}