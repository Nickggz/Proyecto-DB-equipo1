package com.proyecto.Backend.controller;

import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.entity.Circuito;
import com.proyecto.Backend.repository.UsuarioRepository;
import com.proyecto.Backend.repository.CircuitoRepository;
import com.proyecto.Backend.service.CircuitoValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CircuitoRepository circuitoRepository;
    
    @Autowired
    private CircuitoValidationService circuitoValidationService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = loginData.get("cedula");
            String credencialCivica = loginData.get("credencialCivica");
            
            // Validar que se proporcionen ambos campos
            if (cedula == null || cedula.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La cédula es requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (credencialCivica == null || credencialCivica.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La credencial cívica es requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Buscar usuario por cédula
            Usuario usuario = usuarioRepository.findByCedula(cedula.trim());
            
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado con esa cédula");
                return ResponseEntity.ok(response);
            }
            
            // Verificar credencial cívica
            if (!credencialCivica.trim().equals(usuario.getCredencialCivica())) {
                response.put("success", false);
                response.put("message", "Credencial cívica incorrecta");
                return ResponseEntity.ok(response);
            }
            
            // Determinar circuito correcto basado en credencial cívica
            Long circuitoCorrectoId = determinarCircuitoPorCredencial(credencialCivica);
            Circuito circuitoCorrecto = circuitoRepository.findById(circuitoCorrectoId).orElse(null);
            
            if (circuitoCorrecto == null) {
                response.put("success", false);
                response.put("message", "Error: No se pudo determinar el circuito electoral");
                return ResponseEntity.ok(response);
            }
            
            // Actualizar el circuito del usuario si no está asignado o es incorrecto
            if (usuario.getCircuito() == null || !usuario.getCircuito().getId().equals(circuitoCorrectoId)) {
                usuario.setCircuito(circuitoCorrecto);
                usuarioRepository.save(usuario);
            }
            
            // Determinar mesa correspondiente
            Long mesaId = circuitoValidationService.obtenerMesaPorCredencial(credencialCivica);
            
            // Login exitoso
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            response.put("rol", usuario.getRol().toString());
            response.put("esPresidente", usuario.esPresidenteMesa());
            response.put("esMiembroMesa", usuario.esMiembroMesa());  
            response.put("esAdmin", usuario.esAdmin());
            response.put("mesaId", mesaId);
            
            // Incluir información completa del circuito
            Map<String, Object> circuitoInfo = new HashMap<>();
            circuitoInfo.put("id", circuitoCorrecto.getId());
            circuitoInfo.put("nombre", circuitoCorrecto.getNombre());
            circuitoInfo.put("numero", circuitoCorrecto.getNumero());
            circuitoInfo.put("departamento", circuitoCorrecto.getDepartamento());
            
            // Obtener información del establecimiento si existe
            if (circuitoCorrecto.getEstablecimiento() != null) {
                Map<String, Object> establecimientoInfo = new HashMap<>();
                establecimientoInfo.put("nombre", circuitoCorrecto.getEstablecimiento().getNombre());
                establecimientoInfo.put("direccion", circuitoCorrecto.getEstablecimiento().getDireccion());
                circuitoInfo.put("establecimiento", establecimientoInfo);
            }
            
            response.put("circuito", circuitoInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = (String) userData.get("cedula");
            String credencialCivica = (String) userData.get("credencialCivica");
            String nombre = (String) userData.get("nombre");
            String email = (String) userData.get("email");
            
            // Validaciones básicas
            if (cedula == null || cedula.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La cédula es requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (credencialCivica == null || credencialCivica.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La credencial cívica es requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (nombre == null || nombre.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El nombre es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El email es requerido");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Verificar si ya existe usuario con esa cédula
            if (usuarioRepository.findByCedula(cedula.trim()) != null) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario registrado con esa cédula");
                return ResponseEntity.ok(response);
            }
            
            // Verificar si ya existe usuario con esa credencial cívica
            if (usuarioRepository.findByCredencialCivica(credencialCivica.trim()) != null) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario registrado con esa credencial cívica");
                return ResponseEntity.ok(response);
            }
            
            // Verificar si ya existe usuario con ese email
            if (email != null && !email.trim().isEmpty() && usuarioRepository.findByEmail(email.trim()) != null) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario registrado con ese email");
                return ResponseEntity.ok(response);
            }
            
            // Determinar circuito basado en credencial cívica
            Long circuitoId = determinarCircuitoPorCredencial(credencialCivica);
            Circuito circuito = circuitoRepository.findById(circuitoId).orElse(null);
            
            if (circuito == null) {
                response.put("success", false);
                response.put("message", "Error: No se pudo determinar el circuito electoral para esa credencial");
                return ResponseEntity.ok(response);
            }
            
            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setCedula(cedula.trim());
            nuevoUsuario.setCredencialCivica(credencialCivica.trim().toUpperCase());
            nuevoUsuario.setNombre(nombre.trim());
            nuevoUsuario.setEmail(email.trim());
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setCircuito(circuito);
            
            // Guardar usuario
            usuarioRepository.save(nuevoUsuario);
            
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("circuito", circuito.getNombre() + " (" + circuito.getDepartamento() + ")");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/validar-credencial")
    public ResponseEntity<Map<String, Object>> validarCredencial(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String credencialCivica = data.get("credencialCivica");
            
            if (credencialCivica == null || credencialCivica.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Credencial cívica requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Determinar circuito por credencial
            Long circuitoId = determinarCircuitoPorCredencial(credencialCivica);
            Circuito circuito = circuitoRepository.findById(circuitoId).orElse(null);
            
            if (circuito == null) {
                response.put("success", false);
                response.put("message", "Credencial cívica no válida");
                return ResponseEntity.ok(response);
            }
            
            response.put("success", true);
            response.put("circuito", circuito.getNombre());
            response.put("departamento", circuito.getDepartamento());
            response.put("circuitoId", circuito.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al validar credencial");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Método auxiliar para determinar circuito basado en credencial cívica
    private Long determinarCircuitoPorCredencial(String credencialCivica) {
        String credencial = credencialCivica.trim().toUpperCase();
        
        if (credencial.startsWith("AAA")) {
            return 1L; // Montevideo Centro
        } else if (credencial.startsWith("AAB")) {
            return 2L; // Montevideo Pocitos
        } else if (credencial.startsWith("ABC")) {
            return 3L; // Canelones Norte
        }
        
        // Default: Montevideo Centro
        return 1L;
    }
}