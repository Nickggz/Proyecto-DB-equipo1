package com.proyecto.Backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "circuito")
public class Circuito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String numero;
    
    @Column(length = 100)
    private String nombre;
    
    @Column(length = 50)
    private String departamento;
    
    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento departamentoEntity;
    
    @ManyToOne
    @JoinColumn(name = "id_establecimiento")
    private Establecimiento establecimiento;
    
    public Circuito() {}
    
    public Circuito(String numero, String nombre, Departamento departamento) {
        this.numero = numero;
        this.nombre = nombre;
        this.departamentoEntity = departamento;
        if (departamento != null) {
            this.departamento = departamento.getNombre();
        }
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    
    public Departamento getDepartamentoEntity() { return departamentoEntity; }
    public void setDepartamentoEntity(Departamento departamentoEntity) { 
        this.departamentoEntity = departamentoEntity;
        if (departamentoEntity != null) {
            this.departamento = departamentoEntity.getNombre();
        }
    }
    
    public Establecimiento getEstablecimiento() { return establecimiento; }
    public void setEstablecimiento(Establecimiento establecimiento) { this.establecimiento = establecimiento; }
}