package com._lucas.alugaqui.models.Usuario;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String nome;

    @Email
    private String email;

    @NotBlank
    private String senha;

    @NotBlank
    @Size(max = 13) //55 00 9 12345678
    private String telefone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "locador")
    @NotNull
    private List<Casa> casas = new ArrayList<>();

    @OneToMany(mappedBy = "locador")
    @NotNull
    private List<Aluguel> locadorAlugueis;

    @OneToMany(mappedBy = "locatario")
    private List<Aluguel> locatarioAlugueis;

    @OneToMany(mappedBy = "locatario")
    private List<Interesse> interesses;

    public Usuario(String nome, String email, String telefone, Role role, String senha) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.casas = new ArrayList<>();
        this.locadorAlugueis = new ArrayList<>();
        this.locatarioAlugueis = new ArrayList<>();
        this.interesses = new ArrayList<>();
        this.senha = senha;
    }

    public Usuario() {
        this.casas = new ArrayList<>();
        this.locadorAlugueis = new ArrayList<>();
        this.locatarioAlugueis = new ArrayList<>();
        this.interesses = new ArrayList<>();
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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

    public List<Aluguel> getLocatarioAlugueis() {
        return locatarioAlugueis;
    }

    public void setLocatarioAlugueis(List<Aluguel> locatarioAlugueis) {
        this.locatarioAlugueis = locatarioAlugueis;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Interesse> getInteresses() {
        return interesses;
    }

    public void setInteresses(List<Interesse> interesses) {
        this.interesses = interesses;
    }
}