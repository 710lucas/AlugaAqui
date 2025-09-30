package com._lucas.alugaqui.models.Casa;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "casas")
public class Casa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    @NotNull
    private Usuario locador;

    @NotBlank
    private String nome;

    @NotBlank
    private String descricao;

    @NotBlank
    private String endereco;

    @NotBlank
    private String tipo;

    @Positive
    private int quartos;

    private boolean mobiliada;

    @NotNull
    private Status status;

    @NotNull
    private List<String> restricoes;

    @OneToMany(mappedBy = "casa")
    @NotNull
    private List<Aluguel> alugueis;

    @OneToMany(mappedBy = "casa")
    @NotNull
    private List<Interesse> interesses;

    public Casa(Usuario locador, String nome, String descricao, String endereco, String tipo, int quartos, boolean mobiliada, Status status, List<String> restricoes) {
        this.locador = locador;
        this.nome = nome;
        this.descricao = descricao;
        this.endereco = endereco;
        this.tipo = tipo;
        this.quartos = quartos;
        this.mobiliada = mobiliada;
        this.status = status;
        this.restricoes = restricoes;
        this.alugueis = new ArrayList<>();
        this.interesses = new ArrayList<>();
    }

    public Casa() {
        this.alugueis = new ArrayList<>();
        this.interesses = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Usuario getLocador() {
        return locador;
    }

    public void setLocador(Usuario locador) {
        this.locador = locador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQuartos() {
        return quartos;
    }

    public void setQuartos(int quartos) {
        this.quartos = quartos;
    }

    public boolean isMobiliada() {
        return mobiliada;
    }

    public void setMobiliada(boolean mobiliada) {
        this.mobiliada = mobiliada;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(List<String> restricoes) {
        this.restricoes = restricoes;
    }

    public List<Aluguel> getAlugueis() {
        return alugueis;
    }

    public void setAlugueis(List<Aluguel> alugueis) {
        this.alugueis = alugueis;
    }

    public List<Interesse> getInteresses() {
        return interesses;
    }

    public void setInteresses(List<Interesse> interesses) {
        this.interesses = interesses;
    }
}