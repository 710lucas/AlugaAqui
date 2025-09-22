package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;

import java.util.List;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role role;
    private List<Casa> casas;
    private List<Aluguel> locadorAlugueis;
    private Aluguel locatarioAluguel;
    private List<Interesse> interesses;

    public static UsuarioResponseDTO fromModel (Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getRole(),
                usuario.getCasas(),
                usuario.getLocadorAlugueis(),
                usuario.getLocatarioAluguel(),
                usuario.getInteresses()
        );
    }

    public UsuarioResponseDTO(Long id, String nome, String email, String telefone, Role role, List<Casa> casas, List<Aluguel> locadorAlugueis, Aluguel locatarioAluguel, List<Interesse> interesses) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.casas = casas;
        this.locadorAlugueis = locadorAlugueis;
        this.locatarioAluguel = locatarioAluguel;
        this.interesses = interesses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Casa> getCasas() {
        return casas;
    }

    public void setCasas(List<Casa> casas) {
        this.casas = casas;
    }

    public List<Aluguel> getLocadorAlugueis() {
        return locadorAlugueis;
    }

    public void setLocadorAlugueis(List<Aluguel> locadorAlugueis) {
        this.locadorAlugueis = locadorAlugueis;
    }

    public Aluguel getLocatarioAluguel() {
        return locatarioAluguel;
    }

    public void setLocatarioAluguel(Aluguel locatarioAluguel) {
        this.locatarioAluguel = locatarioAluguel;
    }

    public List<Interesse> getInteresses() {
        return interesses;
    }

    public void setInteresses(List<Interesse> interesses) {
        this.interesses = interesses;
    }
}