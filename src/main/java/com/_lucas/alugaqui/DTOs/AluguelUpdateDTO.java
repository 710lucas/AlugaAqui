package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Aluguel.StatusAluguel;

import java.time.LocalDate;

public class AluguelUpdateDTO {

    private StatusAluguel status;

    private Double valor;

    private LocalDate dataFim;

    public AluguelUpdateDTO(StatusAluguel status, double valor, LocalDate dataFim) {
        this.status = status;
        this.valor = valor;
        this.dataFim = dataFim;
    }

    public AluguelUpdateDTO() {
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
