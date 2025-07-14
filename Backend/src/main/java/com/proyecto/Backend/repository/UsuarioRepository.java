package com.proyecto.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.Backend.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCedula(String cedula);
    Usuario findByCredencialCivica(String credencialCivica);
    Usuario findByEmail(String email);
    Usuario findByCedulaAndCredencialCivica(String cedula, String credencialCivica);
}