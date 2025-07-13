package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Voto;
import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    
    // Contar votos por lista en una elección
    @Query("SELECT v.lista.id, COUNT(v) FROM Voto v WHERE v.eleccion.id = :eleccionId AND v.lista IS NOT NULL GROUP BY v.lista.id")
    List<Object[]> contarVotosPorLista(@Param("eleccionId") Long eleccionId);
    
    // Contar votos en blanco
    @Query("SELECT COUNT(v) FROM Voto v WHERE v.eleccion.id = :eleccionId AND v.votoEnBlanco = true")
    Long contarVotosEnBlanco(@Param("eleccionId") Long eleccionId);
    
    // Total de votos en una elección
    Long countByEleccionId(Long eleccionId);
}