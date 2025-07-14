package com.proyecto.Backend.entity;

public enum RolUsuario {
    VOTANTE("Ciudadano habilitado para votar"),
    PRESIDENTE_MESA("Presidente de mesa de votación"),
    SECRETARIO_MESA("Secretario de mesa de votación"),
    VOCAL_MESA("Vocal de mesa de votación"),
    ADMIN("Administrador del sistema");
    
    private final String descripcion;
    
    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}