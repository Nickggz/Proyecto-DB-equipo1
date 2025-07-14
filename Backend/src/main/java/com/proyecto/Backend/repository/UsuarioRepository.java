package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.entity.RolUsuario;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Métodos originales (que ya tienes)
    Usuario findByCedula(String cedula);
    Usuario findByCredencialCivica(String credencialCivica);
    Usuario findByEmail(String email);
    Usuario findByCedulaAndCredencialCivica(String cedula, String credencialCivica);
    
    // NUEVOS: Métodos para manejo de roles
    List<Usuario> findByRol(RolUsuario rol);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = 'PRESIDENTE_MESA'")
    List<Usuario> findAllPresidentes();
    
    @Query("SELECT u FROM Usuario u WHERE u.rol IN ('PRESIDENTE_MESA', 'SECRETARIO_MESA', 'VOCAL_MESA')")
    List<Usuario> findAllMiembrosMesa();
    
    @Query("SELECT u FROM Usuario u WHERE u.cedula = :cedula AND u.rol = 'PRESIDENTE_MESA'")
    Optional<Usuario> findPresidenteByCedula(@Param("cedula") String cedula);
    
    // Verificar si existe un presidente para un circuito específico
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.circuito.id = :circuitoId AND u.rol = 'PRESIDENTE_MESA'")
    boolean existePresidenteEnCircuito(@Param("circuitoId") Long circuitoId);
    
    // Buscar usuarios por rol y circuito
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.circuito.id = :circuitoId")
    List<Usuario> findByRolAndCircuitoId(@Param("rol") RolUsuario rol, @Param("circuitoId") Long circuitoId);
    
    // Verificar si un usuario puede votar (es votante o miembro de mesa)
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.cedula = :cedula AND u.credencialCivica = :credencial AND u.rol IN ('VOTANTE', 'PRESIDENTE_MESA', 'SECRETARIO_MESA', 'VOCAL_MESA')")
    boolean puedeVotar(@Param("cedula") String cedula, @Param("credencial") String credencial);
}