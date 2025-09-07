package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Interesse.StatusInteresse;

public class InteresseUpdateDTO {

    public StatusInteresse status;

    public InteresseUpdateDTO(StatusInteresse status) {
        this.status = status;
    }

    public InteresseUpdateDTO() {
    }

    public StatusInteresse getStatus() {
        return status;
    }

    public void setStatus(StatusInteresse status) {
        this.status = status;
    }
}
