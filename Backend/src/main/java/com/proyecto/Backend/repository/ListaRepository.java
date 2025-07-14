package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Lista;
import com.proyecto.Backend.entity.Eleccion;
import java.util.List;

@Repository
public interface ListaRepository extends JpaRepository<Lista, Long> {
    List<Lista> findByEleccionAndActivaTrueOrderByNumero(Eleccion eleccion);
    List<Lista> findByEleccionIdAndActivaTrueOrderByNumero(Long eleccionId);
    List<Lista> findByEleccionId(Long eleccionId);
}