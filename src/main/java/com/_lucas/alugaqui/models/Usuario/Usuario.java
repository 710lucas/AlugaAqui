package com._lucas.alugaqui.models.Usuario;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Interesse.Interesse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    @Max(13) //55 00 9 12345678
    private String telefone;

    @NotNull
    private Role role;

    @OneToMany(mappedBy = "locador")
    private List<Casa> casas;

    @OneToMany(mappedBy = "locador")
    private List<Aluguel> locadorAlugueis;

    @OneToOne(mappedBy = "locatario")
    private Aluguel locatarioAluguel;

    @OneToMany(mappedBy = "locatario")
    private List<Interesse> interesses;

    public Usuario(String nome, String email, String telefone, Role role) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.role = role;
        this.casas = new ArrayList<>();
        this.locadorAlugueis = new ArrayList<>();
        this.interesses = new ArrayList<>();
    }

    public Usuario() {
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

    public Aluguel getLocatarioAluguel() {
        return locatarioAluguel;
    }

    public void setLocatarioAluguel(Aluguel locatarioAluguel) {
        this.locatarioAluguel = locatarioAluguel;
    }
}
