package com._lucas.alugaqui.DTOs;

import jakarta.validation.constraints.Max;

public class UsuarioUpdateDTO {

    private String nome;

    private String email;

    @Max(13)
    private String telefone;

    public UsuarioUpdateDTO(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public UsuarioUpdateDTO() {
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
}
