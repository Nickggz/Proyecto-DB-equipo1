package com.proyecto.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mesa")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String numero;
    
    @ManyToOne
    @JoinColumn(name = "id_circuito")
    private Circuito circuito;
    
    @Column(name = "capacidad_votantes")
    private Integer capacidadVotantes = 500;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "cerrada", columnDefinition = "boolean default false")
    private Boolean cerrada = false;
    
    // Constructores
    public Mesa() {}
    
    public Mesa(String numero, Circuito circuito) {
        this.numero = numero;
        this.circuito = circuito;
        this.capacidadVotantes = 500;
        this.cerrada = false;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public Circuito getCircuito() { return circuito; }
    public void setCircuito(Circuito circuito) { this.circuito = circuito; }
    
    public Integer getCapacidadVotantes() { return capacidadVotantes; }
    public void setCapacidadVotantes(Integer capacidadVotantes) { this.capacidadVotantes = capacidadVotantes; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Boolean getCerrada() { return cerrada; }
    public void setCerrada(Boolean cerrada) { this.cerrada = cerrada; }
}