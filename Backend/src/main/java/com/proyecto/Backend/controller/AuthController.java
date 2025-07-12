package com.proyecto.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.repository.UsuarioRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = request.get("cedula");
            String nombre = request.get("nombre");
            String email = request.get("email");
            
            // Verificar si ya existe
            if (usuarioRepository.existsByCedula(cedula)) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario con esta cédula");
                return response;
            }
            
            // Crear nuevo usuario
            Usuario usuario = new Usuario(cedula, nombre, email);
            usuarioRepository.save(usuario);
            
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = request.get("cedula");
            
            Optional<Usuario> usuario = usuarioRepository.findByCedula(cedula);
            
            if (usuario.isPresent()) {
                response.put("success", true);
                response.put("nombre", usuario.get().getNombre());
                response.put("email", usuario.get().getEmail());
                response.put("message", "Login exitoso");
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al iniciar sesión: " + e.getMessage());
        }
        
        return response;
    }
}