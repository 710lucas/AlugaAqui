package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CasaCreateDTO {

    @NotNull
    private Long locadorId;

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
    private List<String> restricoes;

    public CasaCreateDTO(Long locadorId, String nome, String descricao, String endereco, String tipo, int quartos, boolean mobiliada, List<String> restricoes) {
        this.locadorId = locadorId;
        this.nome = nome;
        this.descricao = descricao;
        this.endereco = endereco;
        this.tipo = tipo;
        this.quartos = quartos;
        this.mobiliada = mobiliada;
        this.restricoes = restricoes;
    }

    public CasaCreateDTO() {
    }

    public Long getLocadorId() {
        return locadorId;
    }

    public void setLocadorId(Long locadorId) {
        this.locadorId = locadorId;
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

    public List<String> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(List<String> restricoes) {
        this.restricoes = restricoes;
    }
}
