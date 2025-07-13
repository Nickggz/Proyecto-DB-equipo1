package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Eleccion;
import java.util.List;

@Repository
public interface EleccionRepository extends JpaRepository<Eleccion, Long> {
    List<Eleccion> findByActivaTrue();
    List<Eleccion> findByActivaTrueOrderByFechaDesc();
}