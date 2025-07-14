package com.proyecto.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.proyecto.Backend.entity.*;
import com.proyecto.Backend.repository.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/circuito")
@CrossOrigin(origins = "http://localhost:3000")
public class CircuitoController {
    
    @Autowired
    private DepartamentoRepository departamentoRepository;
    
    @Autowired
    private CircuitoRepository circuitoRepository;
    
    @Autowired
    private EstablecimientoRepository establecimientoRepository;
    
    // Obtener todos los departamentos
    @GetMapping("/departamentos")
    public Map<String, Object> getDepartamentos() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Departamento> departamentos = departamentoRepository.findAll();
            response.put("success", true);
            response.put("departamentos", departamentos);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener departamentos: " + e.getMessage());
        }
        
        return response;
    }
    
    // Obtener circuitos por departamento
    @GetMapping("/departamento/{departamentoId}")
    public Map<String, Object> getCircuitosPorDepartamento(@PathVariable Long departamentoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Circuito> circuitos = circuitoRepository.findByDepartamentoEntityId(departamentoId);
            response.put("success", true);
            response.put("circuitos", circuitos);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener circuitos: " + e.getMessage());
        }
        
        return response;
    }
    
    // Obtener todos los circuitos
    @GetMapping("/todos")
    public Map<String, Object> getTodosCircuitos() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Circuito> circuitos = circuitoRepository.findAll();
            response.put("success", true);
            response.put("circuitos", circuitos);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener circuitos: " + e.getMessage());
        }
        
        return response;
    }
    
    // Obtener información detallada de un circuito
    @GetMapping("/{circuitoId}")
    public Map<String, Object> getCircuito(@PathVariable Long circuitoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var circuito = circuitoRepository.findById(circuitoId);
            if (circuito.isPresent()) {
                response.put("success", true);
                response.put("circuito", circuito.get());
            } else {
                response.put("success", false);
                response.put("message", "Circuito no encontrado");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener circuito: " + e.getMessage());
        }
        
        return response;
    }
}