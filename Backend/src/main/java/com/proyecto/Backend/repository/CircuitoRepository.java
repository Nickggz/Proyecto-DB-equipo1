package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Circuito;
import java.util.List;

@Repository
public interface CircuitoRepository extends JpaRepository<Circuito, Long> {
    List<Circuito> findByDepartamentoEntityId(Long departamentoId);
}