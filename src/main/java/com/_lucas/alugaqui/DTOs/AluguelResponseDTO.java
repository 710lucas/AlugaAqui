package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import java.time.LocalDate;

public class AluguelResponseDTO {
    private Long id;
    private Long casaId;
    private UsuarioSimpleDTO locador;
    private UsuarioSimpleDTO locatario;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valor;
    private StatusAluguel status;
    private String contratoUrl;

    public AluguelResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCasaId() { return casaId; }
    public void setCasaId(Long casaId) { this.casaId = casaId; }
    public UsuarioSimpleDTO getLocador() { return locador; }
    public void setLocador(UsuarioSimpleDTO locador) { this.locador = locador; }
    public UsuarioSimpleDTO getLocatario() { return locatario; }
    public void setLocatario(UsuarioSimpleDTO locatario) { this.locatario = locatario; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public StatusAluguel getStatus() { return status; }
    public void setStatus(StatusAluguel status) { this.status = status; }
    public String getContratoUrl() { return contratoUrl; }
    public void setContratoUrl(String contratoUrl) { this.contratoUrl = contratoUrl; }
}