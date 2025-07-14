package com.proyecto.Backend.service;

import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CircuitoValidationService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public String obtenerTipoVoto(String credencialCivica, Long circuitoVotacionId) {
        if (credencialCivica == null) return "VALIDO";
        
        Long circuitoCorrectoId = determinarCircuitoPorCredencial(credencialCivica);
        
        if (circuitoCorrectoId.equals(circuitoVotacionId)) {
            return "VALIDO";
        } else {
            return "OBSERVADO";
        }
    }
    
    private Long determinarCircuitoPorCredencial(String credencialCivica) {
        if (credencialCivica.startsWith("AAA")) {
            return 1L; // Montevideo Centro
        } else if (credencialCivica.startsWith("AAB")) {
            return 2L; // Montevideo Pocitos
        } else if (credencialCivica.startsWith("ABC")) {
            return 3L; // Canelones Norte
        }
        return 1L; // Default
    }
    
    public Long obtenerMesaPorCredencial(String credencialCivica) {
        if (credencialCivica == null) return 1L;
        
        if (credencialCivica.startsWith("AAA")) {
            return 1L; // Mesa 001A
        } else if (credencialCivica.startsWith("AAB")) {
            return 2L; // Mesa 001B
        } else if (credencialCivica.startsWith("ABC")) {
            return 3L; // Mesa 001C
        }
        return 1L; // Default
    }
}