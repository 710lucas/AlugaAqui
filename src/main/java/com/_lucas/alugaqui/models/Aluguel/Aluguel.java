package com._lucas.alugaqui.models.Aluguel;

import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "alugueis")
public class Aluguel {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    private Casa casa;

    @ManyToOne()
    private Usuario locador;

    @OneToOne()
    private Usuario locatario;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private double valor;

    private StatusAluguel status;

    private String contratoUrl;

    public Aluguel(Casa casa, Usuario locador, Usuario locatario, LocalDate dataInicio, LocalDate dataFim, double valor, StatusAluguel status, String contratoUrl) {
        this.casa = casa;
        this.locador = locador;
        this.locatario = locatario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valor = valor;
        this.status = status;
        this.contratoUrl = contratoUrl;
    }

    public Aluguel() {
    }

    public Long getId() {
        return id;
    }

    public Casa getCasa() {
        return casa;
    }

    public void setCasa(Casa casa) {
        this.casa = casa;
    }

    public Usuario getLocador() {
        return locador;
    }

    public void setLocador(Usuario locador) {
        this.locador = locador;
    }

    public Usuario getLocatario() {
        return locatario;
    }

    public void setLocatario(Usuario locatario) {
        this.locatario = locatario;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public String getContratoUrl() {
        return contratoUrl;
    }

    public void setContratoUrl(String contratoUrl) {
        this.contratoUrl = contratoUrl;
    }
}
