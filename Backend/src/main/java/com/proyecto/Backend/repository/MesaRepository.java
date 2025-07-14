package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Mesa;
import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    // Método original (que ya tienes)
    Mesa findByNumero(String numero);
    
    // NUEVOS: Métodos para control de mesas
    List<Mesa> findByCerrada(Boolean cerrada);
    
    @Query("SELECT m FROM Mesa m WHERE m.cerrada = false")
    List<Mesa> findMesasAbiertas();
    
    @Query("SELECT m FROM Mesa m WHERE m.cerrada = true")
    List<Mesa> findMesasCerradas();
    
    @Query("SELECT m FROM Mesa m WHERE m.presidenteCedula = :cedula")
    List<Mesa> findMesasByPresidente(@Param("cedula") String cedulaPresidente);
    
    @Query("SELECT m FROM Mesa m WHERE m.id = :mesaId AND m.presidenteCedula = :cedula")
    Optional<Mesa> findMesaByIdAndPresidente(@Param("mesaId") Long mesaId, @Param("cedula") String cedulaPresidente);
    
    // Verificar si una mesa puede ser cerrada por un presidente específico
    @Query("SELECT COUNT(m) > 0 FROM Mesa m WHERE m.id = :mesaId AND m.presidenteCedula = :cedula AND m.cerrada = false")
    boolean puedeSerCerradaPor(@Param("mesaId") Long mesaId, @Param("cedula") String cedulaPresidente);
    
    // Encontrar mesas por circuito
    @Query("SELECT m FROM Mesa m WHERE m.circuito.id = :circuitoId")
    List<Mesa> findByCircuitoId(@Param("circuitoId") Long circuitoId);
    
    // Verificar si una mesa está cerrada
    @Query("SELECT m.cerrada FROM Mesa m WHERE m.id = :mesaId")
    Boolean isCerrada(@Param("mesaId") Long mesaId);
    
    // Obtener estado de las mesas con información completa
    @Query("SELECT m.id, m.numero, m.cerrada, m.totalVotosEmitidos, m.presidenteNombre, c.nombre as circuito " +
           "FROM Mesa m LEFT JOIN m.circuito c")
    List<Object[]> findEstadoMesas();
}