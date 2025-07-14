package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 8)
    private String cedula;
    
    @Column(name = "credencial_civica", unique = true, length = 20)
    private String credencialCivica;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(unique = true, length = 100)
    private String email;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // NUEVO: Campo rol usando ENUM
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol = RolUsuario.VOTANTE;
    
    @ManyToOne
    @JoinColumn(name = "id_circuito")
    private Circuito circuito;

    // Constructor vacío
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Usuario(String cedula, String credencialCivica, String nombre, String email) {
        this.cedula = cedula;
        this.credencialCivica = credencialCivica;
        this.nombre = nombre;
        this.email = email;
        this.fechaRegistro = LocalDateTime.now();
        this.rol = RolUsuario.VOTANTE;
    }
    public boolean esPresidenteMesa() {
        return this.rol == RolUsuario.PRESIDENTE_MESA;
    }
    
    public boolean esMiembroMesa() {
        return this.rol == RolUsuario.PRESIDENTE_MESA || 
               this.rol == RolUsuario.SECRETARIO_MESA || 
               this.rol == RolUsuario.VOCAL_MESA;
    }
    
    public boolean esAdmin() {
        return this.rol == RolUsuario.ADMIN;
    }

    // Getters y Setters (todos los originales + rol)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getCredencialCivica() { return credencialCivica; }
    public void setCredencialCivica(String credencialCivica) { this.credencialCivica = credencialCivica; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    // NUEVO: Getter y setter para rol
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public Circuito getCircuito() { return circuito; }
    public void setCircuito(Circuito circuito) { this.circuito = circuito; }
}