package com.proyecto.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MESA")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String numero;
    
    @Column(name = "capacidad_votantes")
    private Integer capacidadVotantes = 500;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // Constructores
    public Mesa() {}
    
    public Mesa(String numero) {
        this.numero = numero;
        this.capacidadVotantes = 500;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public Integer getCapacidadVotantes() { return capacidadVotantes; }
    public void setCapacidadVotantes(Integer capacidadVotantes) { this.capacidadVotantes = capacidadVotantes; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}