package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Usuario.Role;

import java.util.List;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role role;

    private List<Long> casasIds;
    private List<Long> locadorAlugueisIds;
    private List<Long> locatarioAlugueisIds;
    private List<Long> interessesIds;

    public UsuarioResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Long> getCasasIds() { return casasIds; }
    public void setCasasIds(List<Long> casasIds) { this.casasIds = casasIds; }
    public List<Long> getLocadorAlugueisIds() { return locadorAlugueisIds; }
    public void setLocadorAlugueisIds(List<Long> locadorAlugueisIds) { this.locadorAlugueisIds = locadorAlugueisIds; }
    public List<Long> getLocatarioAlugueisIds() { return locatarioAlugueisIds; }
    public void setLocatarioAlugueisIds(List<Long> locatarioAlugueisIds) { this.locatarioAlugueisIds = locatarioAlugueisIds; }
    public List<Long> getInteressesIds() { return interessesIds; }
    public void setInteressesIds(List<Long> interessesIds) { this.interessesIds = interessesIds; }
}