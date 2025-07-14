package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Eleccion;
import java.util.List;
import java.util.Optional;

@Repository
public interface EleccionRepository extends JpaRepository<Eleccion, Long> {
    // Métodos originales (que ya tienes)
    List<Eleccion> findByActivaTrue();
    List<Eleccion> findByActivaTrueOrderByFechaDesc();
    
    // NUEVO: Método adicional para obtener la elección activa actual
    @Query("SELECT e FROM Eleccion e WHERE e.activa = true ORDER BY e.fecha DESC")
    Optional<Eleccion> findEleccionActiva();
}