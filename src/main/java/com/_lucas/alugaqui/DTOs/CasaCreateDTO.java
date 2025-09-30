package com._lucas.alugaqui.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(description = "DTO para criação de uma nova casa/imóvel por um Locador.")
public class CasaCreateDTO {

    @NotBlank
    @Schema(description = "Nome para identificar a casa.")
    private String nome;

    @NotBlank
    @Schema(description = "Descrição detalhada da casa.")
    private String descricao;

    @NotBlank
    @Schema(description = "Endereço completo da casa.")
    private String endereco;

    @NotBlank
    @Schema(description = "Tipo de imóvel (Ex: Apartamento, Casa, Studio).")
    private String tipo;

    @Positive
    @Schema(description = "Número de quartos na casa.")
    private int quartos;

    @Schema(description = "Indica se a casa é mobiliada.")
    private boolean mobiliada;

    @NotNull
    @Schema(description = "Lista de restrições ou regras da casa.")
    private List<String> restricoes;

    public CasaCreateDTO(String nome, String descricao, String endereco, String tipo, int quartos, boolean mobiliada, List<String> restricoes) {
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