package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.ControlVoto;
import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.entity.Eleccion;
import java.util.Optional;

@Repository
public interface ControlVotoRepository extends JpaRepository<ControlVoto, Long> {
    
    // Verificar si un ciudadano ya votó en una elección
    boolean existsByCiudadanoAndEleccion(Usuario ciudadano, Eleccion eleccion);
    
    // Obtener el control de voto de un ciudadano en una elección
    Optional<ControlVoto> findByCiudadanoAndEleccion(Usuario ciudadano, Eleccion eleccion);
    
    // Contar cuántas personas han votado en una elección
    Long countByEleccionId(Long eleccionId);
}