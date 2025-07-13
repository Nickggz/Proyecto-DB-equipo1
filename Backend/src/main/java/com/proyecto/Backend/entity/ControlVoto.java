package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTROL_VOTO",
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
    
    @Column(name = "fecha_hora_voto")
    private LocalDateTime fechaHoraVoto;
    
    // Constructores
    public ControlVoto() {
        this.fechaHoraVoto = LocalDateTime.now();
    }
    
    public ControlVoto(Usuario ciudadano, Eleccion eleccion, Mesa mesa) {
        this.ciudadano = ciudadano;
        this.eleccion = eleccion;
        this.mesa = mesa;
        this.fechaHoraVoto = LocalDateTime.now();
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getCiudadano() { return ciudadano; }
    public void setCiudadano(Usuario ciudadano) { this.ciudadano = ciudadano; }
    
    public Eleccion getEleccion() { return eleccion; }
    public void setEleccion(Eleccion eleccion) { this.eleccion = eleccion; }
    
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    
    public LocalDateTime getFechaHoraVoto() { return fechaHoraVoto; }
    public void setFechaHoraVoto(LocalDateTime fechaHoraVoto) { this.fechaHoraVoto = fechaHoraVoto; }
}