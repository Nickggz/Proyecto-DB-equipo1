package com.proyecto.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "control_voto", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_ciudadano", "id_eleccion"}))
public class ControlVoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_ciudadano", nullable = false)
    private Usuario ciudadano;
    
    @ManyToOne
    @JoinColumn(name = "id_eleccion", nullable = false)
    private Eleccion eleccion;
    
    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;

    // Constructores
    public ControlVoto() {}

    public ControlVoto(Usuario ciudadano, Eleccion eleccion, Mesa mesa) {
        this.ciudadano = ciudadano;
        this.eleccion = eleccion;
        this.mesa = mesa;
    }

    // Getters y Setters (SIN fechaHoraVoto)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getCiudadano() { return ciudadano; }
    public void setCiudadano(Usuario ciudadano) { this.ciudadano = ciudadano; }

    public Eleccion getEleccion() { return eleccion; }
    public void setEleccion(Eleccion eleccion) { this.eleccion = eleccion; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
}