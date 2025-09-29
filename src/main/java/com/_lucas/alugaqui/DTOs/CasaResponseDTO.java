package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Casa.Status;
import java.util.List;

public class CasaResponseDTO {
    private Long id;
    private UsuarioSimpleDTO locador;
    private String nome;
    private String descricao;
    private String endereco;
    private String tipo;
    private int quartos;
    private boolean mobiliada;
    private Status status;
    private List<String> restricoes;
    private List<Long> alugueisIds;
    private List<Long> interessesIds;

    public CasaResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UsuarioSimpleDTO getLocador() { return locador; }
    public void setLocador(UsuarioSimpleDTO locador) { this.locador = locador; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuartos() { return quartos; }
    public void setQuartos(int quartos) { this.quartos = quartos; }
    public boolean isMobiliada() { return mobiliada; }
    public void setMobiliada(boolean mobiliada) { this.mobiliada = mobiliada; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<String> getRestricoes() { return restricoes; }
    public void setRestricoes(List<String> restricoes) { this.restricoes = restricoes; }
    public List<Long> getAlugueisIds() { return alugueisIds; }
    public void setAlugueisIds(List<Long> alugueisIds) { this.alugueisIds = alugueisIds; }
    public List<Long> getInteressesIds() { return interessesIds; }
    public void setInteressesIds(List<Long> interessesIds) { this.interessesIds = interessesIds; }
}