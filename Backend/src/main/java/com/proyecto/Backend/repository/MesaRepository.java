package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Mesa findByNumero(String numero);
}