package com.proyecto.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.entity.Circuito;
import com.proyecto.Backend.repository.UsuarioRepository;
import com.proyecto.Backend.repository.CircuitoRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CircuitoRepository circuitoRepository;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = (String) userData.get("cedula");
            String nombre = (String) userData.get("nombre");
            String email = (String) userData.get("email");
            Long circuitoId = userData.get("circuitoId") != null ? 
                Long.parseLong(userData.get("circuitoId").toString()) : null;
            
            // Validar que no exista ya
            if (usuarioRepository.findByCedula(cedula).isPresent()) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario con esa cédula");
                return ResponseEntity.ok(response);
            }
            
            if (usuarioRepository.findByEmail(email).isPresent()) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario con ese email");
                return ResponseEntity.ok(response);
            }
            
            // Crear usuario
            Usuario usuario = new Usuario(cedula, nombre, email);
            
            // Asignar circuito si se proporcionó
            if (circuitoId != null) {
                Optional<Circuito> circuito = circuitoRepository.findById(circuitoId);
                if (circuito.isPresent()) {
                    usuario.setCircuito(circuito.get());
                }
            }
            
            usuarioRepository.save(usuario);
            
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("circuito", usuario.getCircuito() != null ? 
                usuario.getCircuito().getNombre() : "Sin circuito asignado");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = credentials.get("cedula");
            Optional<Usuario> usuario = usuarioRepository.findByCedula(cedula);
            
            if (usuario.isPresent()) {
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("nombre", usuario.get().getNombre());
                response.put("email", usuario.get().getEmail());
                response.put("circuito", usuario.get().getCircuito() != null ? 
                    Map.of(
                        "id", usuario.get().getCircuito().getId(),
                        "nombre", usuario.get().getCircuito().getNombre(),
                        "numero", usuario.get().getCircuito().getNumero(),
                        "departamento", usuario.get().getCircuito().getDepartamento()
                    ) : null);
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el login: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}