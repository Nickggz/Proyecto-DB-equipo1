package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.ControlVoto;
import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.entity.Eleccion;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlVotoRepository extends JpaRepository<ControlVoto, Long> {
    
    // Métodos originales (que ya tienes)
    boolean existsByCiudadanoAndEleccion(Usuario ciudadano, Eleccion eleccion);
    Optional<ControlVoto> findByCiudadanoAndEleccion(Usuario ciudadano, Eleccion eleccion);
    Long countByEleccionId(Long eleccionId);
    
    // NUEVOS: Métodos adicionales para control
    
    // Verificar si un usuario ya votó usando cédula y credencial
    @Query("SELECT COUNT(cv) > 0 FROM ControlVoto cv " +
           "WHERE cv.ciudadano.cedula = :cedula " +
           "AND cv.ciudadano.credencialCivica = :credencial " +
           "AND cv.eleccion.id = :eleccionId")
    boolean yaVoto(@Param("cedula") String cedula, 
                   @Param("credencial") String credencial, 
                   @Param("eleccionId") Long eleccionId);
    
    // Contar votos por mesa
    @Query("SELECT COUNT(cv) FROM ControlVoto cv WHERE cv.mesa.id = :mesaId")
    Long countByMesaId(@Param("mesaId") Long mesaId);
    
    // Obtener controles de voto por mesa y elección
    @Query("SELECT cv FROM ControlVoto cv WHERE cv.mesa.id = :mesaId AND cv.eleccion.id = :eleccionId")
    List<ControlVoto> findByMesaIdAndEleccionId(@Param("mesaId") Long mesaId, @Param("eleccionId") Long eleccionId);
}