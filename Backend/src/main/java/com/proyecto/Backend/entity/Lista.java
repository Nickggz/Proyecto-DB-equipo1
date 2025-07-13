package com.proyecto.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LISTA")
public class Lista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer numero;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @ManyToOne
    @JoinColumn(name = "id_partido", nullable = false)
    private Partido partido;
    
    @ManyToOne
    @JoinColumn(name = "id_eleccion", nullable = false)
    private Eleccion eleccion;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean activa = true;
    
    // Constructores
    public Lista() {}
    
    public Lista(Integer numero, String nombre, Partido partido, Eleccion eleccion) {
        this.numero = numero;
        this.nombre = nombre;
        this.partido = partido;
        this.eleccion = eleccion;
        this.activa = true;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    
    public Eleccion getEleccion() { return eleccion; }
    public void setEleccion(Eleccion eleccion) { this.eleccion = eleccion; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}