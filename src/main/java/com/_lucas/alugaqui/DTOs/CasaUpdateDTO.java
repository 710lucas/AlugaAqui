package com._lucas.alugaqui.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "DTO para atualização de informações da casa (somente campos não nulos serão alterados).")
public class CasaUpdateDTO {

    @Schema(description = "Novo nome da casa.")
    private String nome;

    @Schema(description = "Nova descrição da casa.")
    private String descricao;

    @Schema(description = "Novo endereço da casa.")
    private String endereco;

    @Schema(description = "Novo tipo de imóvel.")
    private String tipo;

    @Schema(description = "Novo número de quartos.")
    private Integer quartos;

    @Schema(description = "Indica se a casa é mobiliada.")
    private Boolean mobiliada;

    @Schema(description = "Novas restrições ou regras da casa.")
    private List<String> restricoes;

    public CasaUpdateDTO(String nome, String dscricao, String endreco, String tipo, int quartos, boolean mobiliada, List<String> restricoes) {
        this.nome = nome;
        this.descricao = dscricao;
        this.endereco = endreco;
        this.tipo = tipo;
        this.quartos = quartos;
        this.mobiliada = mobiliada;
        this.restricoes = restricoes;
    }

    public CasaUpdateDTO() {
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

    public Integer getQuartos() {
        return quartos;
    }

    public void setQuartos(Integer quartos) {
        this.quartos = quartos;
    }

    public Boolean isMobiliada() {
        return mobiliada;
    }

    public void setMobiliada(Boolean mobiliada) {
        this.mobiliada = mobiliada;
    }

    public List<String> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(List<String> restricoes) {
        this.restricoes = restricoes;
    }
}