package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ELECCION")
public class Eleccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(nullable = false, length = 50)
    private String tipo;
    
    @Column(length = 200)
    private String descripcion;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean activa = true;
    
    // Constructores
    public Eleccion() {}
    
    public Eleccion(LocalDate fecha, String tipo, String descripcion) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.activa = true;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}