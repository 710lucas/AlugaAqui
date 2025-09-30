package com._lucas.alugaqui.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, String id) {
        super(recurso + " não encontrado(a) com identificador: " + id);
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}