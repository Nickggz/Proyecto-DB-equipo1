package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Voto;
import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    
    // Métodos originales (que ya tienes)
    @Query("SELECT v.lista.id, COUNT(v) FROM Voto v WHERE v.eleccion.id = :eleccionId AND v.lista IS NOT NULL GROUP BY v.lista.id")
    List<Object[]> contarVotosPorLista(@Param("eleccionId") Long eleccionId);
    
    @Query("SELECT COUNT(v) FROM Voto v WHERE v.eleccion.id = :eleccionId AND v.votoEnBlanco = true")
    Long contarVotosEnBlanco(@Param("eleccionId") Long eleccionId);
    
    Long countByEleccionId(Long eleccionId);
    
    // NUEVO: Método para contar votos por mesa (necesario para cerrar mesas)
    @Query("SELECT COUNT(v) FROM Voto v WHERE v.mesa.id = :mesaId")
    Integer countByMesaId(@Param("mesaId") Long mesaId);
    
    // NUEVOS: Métodos adicionales para control de tipos de voto y mesas
    
    // Contar votos por tipo
  //@Query("SELECT v.tipoVoto, COUNT(v) FROM Voto v WHERE v.eleccion.id = :eleccionId GROUP BY v.tipoVoto")
  //List<Object[]> contarVotosPorTipo(@Param("eleccionId") Long eleccionId);
    
    // Obtener votos por mesa y elección
    @Query("SELECT v FROM Voto v WHERE v.mesa.id = :mesaId AND v.eleccion.id = :eleccionId")
    List<Voto> findByMesaIdAndEleccionId(@Param("mesaId") Long mesaId, @Param("eleccionId") Long eleccionId);
    
    // Verificar si hay votos en una mesa (para validar cierre)
    @Query("SELECT COUNT(v) > 0 FROM Voto v WHERE v.mesa.id = :mesaId")
    boolean existenVotosEnMesa(@Param("mesaId") Long mesaId);
}
