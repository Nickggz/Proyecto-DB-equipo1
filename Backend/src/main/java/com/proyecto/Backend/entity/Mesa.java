package com.proyecto.Backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    
    // NUEVOS CAMPOS PARA CONTROL DE CIERRE
    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    
    @Column(name = "cerrada_por_cedula", length = 8)
    private String cerradaPorCedula;
    
    @Column(name = "motivo_cierre", columnDefinition = "TEXT")
    private String motivoCierre;
    
    @Column(name = "total_votos_emitidos")
    private Integer totalVotosEmitidos = 0;
    
    // NUEVOS CAMPOS PARA MIEMBROS DE MESA
    @Column(name = "presidente_cedula", length = 8)
    private String presidenteCedula;
    
    @Column(name = "presidente_nombre", length = 100)
    private String presidenteNombre;
    
    @Column(name = "secretario_cedula", length = 8)
    private String secretarioCedula;
    
    @Column(name = "secretario_nombre", length = 100)
    private String secretarioNombre;
    
    @Column(name = "vocal_cedula", length = 8)
    private String vocalCedula;
    
    @Column(name = "vocal_nombre", length = 100)
    private String vocalNombre;
    
    // Constructores
    public Mesa() {}
    
    public Mesa(String numero, Circuito circuito) {
        this.numero = numero;
        this.circuito = circuito;
        this.capacidadVotantes = 500;
        this.cerrada = false;
        this.totalVotosEmitidos = 0;
    }
    
    // NUEVOS: Métodos de utilidad
    public boolean estaAbierta() {
        return !this.cerrada;
    }
    
    public boolean puedeSerCerradaPor(String cedula) {
        return this.presidenteCedula != null && this.presidenteCedula.equals(cedula);
    }
    
    public void cerrarMesa(String cedulaPresidente, String motivo, Integer totalVotos) {
        this.cerrada = true;
        this.fechaCierre = LocalDateTime.now();
        this.cerradaPorCedula = cedulaPresidente;
        this.motivoCierre = motivo;
        this.totalVotosEmitidos = totalVotos;
    }
    
    // Getters y setters (todos los originales + nuevos campos)
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
    
    // NUEVOS: Getters y setters para campos de control
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
    
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    
    public String getCerradaPorCedula() { return cerradaPorCedula; }
    public void setCerradaPorCedula(String cerradaPorCedula) { this.cerradaPorCedula = cerradaPorCedula; }
    
    public String getMotivoCierre() { return motivoCierre; }
    public void setMotivoCierre(String motivoCierre) { this.motivoCierre = motivoCierre; }
    
    public Integer getTotalVotosEmitidos() { return totalVotosEmitidos; }
    public void setTotalVotosEmitidos(Integer totalVotosEmitidos) { this.totalVotosEmitidos = totalVotosEmitidos; }
    
    public String getPresidenteCedula() { return presidenteCedula; }
    public void setPresidenteCedula(String presidenteCedula) { this.presidenteCedula = presidenteCedula; }
    
    public String getPresidenteNombre() { return presidenteNombre; }
    public void setPresidenteNombre(String presidenteNombre) { this.presidenteNombre = presidenteNombre; }
    
    public String getSecretarioCedula() { return secretarioCedula; }
    public void setSecretarioCedula(String secretarioCedula) { this.secretarioCedula = secretarioCedula; }
    
    public String getSecretarioNombre() { return secretarioNombre; }
    public void setSecretarioNombre(String secretarioNombre) { this.secretarioNombre = secretarioNombre; }
    
    public String getVocalCedula() { return vocalCedula; }
    public void setVocalCedula(String vocalCedula) { this.vocalCedula = vocalCedula; }
    
    public String getVocalNombre() { return vocalNombre; }
    public void setVocalNombre(String vocalNombre) { this.vocalNombre = vocalNombre; }
}