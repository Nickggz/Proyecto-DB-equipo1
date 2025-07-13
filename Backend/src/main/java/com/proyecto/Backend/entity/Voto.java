package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "VOTO")
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_mesa", nullable = false)
    private Mesa mesa;
    
    @ManyToOne
    @JoinColumn(name = "id_lista")
    private Lista lista; // null si es voto en blanco
    
    @ManyToOne
    @JoinColumn(name = "id_eleccion", nullable = false)
    private Eleccion eleccion;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    @Column(name = "voto_en_blanco", columnDefinition = "boolean default false")
    private Boolean votoEnBlanco = false;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // Constructores
    public Voto() {
        this.fechaHora = LocalDateTime.now();
    }
    
    public Voto(Mesa mesa, Lista lista, Eleccion eleccion, Boolean votoEnBlanco) {
        this.mesa = mesa;
        this.lista = lista;
        this.eleccion = eleccion;
        this.votoEnBlanco = votoEnBlanco;
        this.fechaHora = LocalDateTime.now();
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    
    public Lista getLista() { return lista; }
    public void setLista(Lista lista) { this.lista = lista; }
    
    public Eleccion getEleccion() { return eleccion; }
    public void setEleccion(Eleccion eleccion) { this.eleccion = eleccion; }
    
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    
    public Boolean getVotoEnBlanco() { return votoEnBlanco; }
    public void setVotoEnBlanco(Boolean votoEnBlanco) { this.votoEnBlanco = votoEnBlanco; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}