package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PARTIDO")
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 10)
    private String siglas;
    
    @Column(length = 7)
    private String color;
    
    @Column(name = "fecha_fundacion")
    private LocalDate fechaFundacion;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;
    
    // Constructores
    public Partido() {}
    
    public Partido(String nombre, String siglas, String color) {
        this.nombre = nombre;
        this.siglas = siglas;
        this.color = color;
        this.activo = true;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getSiglas() { return siglas; }
    public void setSiglas(String siglas) { this.siglas = siglas; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public LocalDate getFechaFundacion() { return fechaFundacion; }
    public void setFechaFundacion(LocalDate fechaFundacion) { this.fechaFundacion = fechaFundacion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}