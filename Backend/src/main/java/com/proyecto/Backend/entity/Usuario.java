package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cedula", unique = true, nullable = false, length = 8)
    private String cedula;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    
    @ManyToOne
    @JoinColumn(name = "id_circuito")
    private Circuito circuito;
    
    // Constructores
    public Usuario() {
        this.fechaRegistro = new Date();
    }
    
    public Usuario(String cedula, String nombre, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.email = email;
        this.fechaRegistro = new Date();
    }
    
    // Getters y setters existentes
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    // Nuevos getters y setters para circuito
    public Circuito getCircuito() { 
        return circuito; 
    }
    
    public void setCircuito(Circuito circuito) { 
        this.circuito = circuito; 
    }
}