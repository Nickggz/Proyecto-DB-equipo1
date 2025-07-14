package com.proyecto.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "establecimiento")
public class Establecimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(length = 300)
    private String direccion;
    
    @Column(length = 50)
    private String departamento;
    
    @Column
    private Integer capacidad;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean activo = true;
    
    public Establecimiento() {}
    
    public Establecimiento(String nombre, String direccion, String departamento) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.departamento = departamento;
        this.activo = true;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}