package com._lucas.alugaqui.models.Interesse;

import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Interesse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    private Casa casa;

    @ManyToOne
    private Usuario locatario;

    private StatusInteresse status;

    private LocalDateTime createdAt;

    public Interesse(Casa casa, Usuario locatario, StatusInteresse status, LocalDateTime createdAt) {
        this.casa = casa;
        this.locatario = locatario;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Interesse() {
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

    public Usuario getLocatario() {
        return locatario;
    }

    public void setLocatario(Usuario locatario) {
        this.locatario = locatario;
    }

    public StatusInteresse getStatus() {
        return status;
    }

    public void setStatus(StatusInteresse status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
