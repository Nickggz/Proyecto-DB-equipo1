package com.proyecto.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.proyecto.Backend.entity.*;
import com.proyecto.Backend.repository.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/voto")
@CrossOrigin(origins = "http://localhost:3000")
public class VotoController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EleccionRepository eleccionRepository;
    
    @Autowired
    private ListaRepository listaRepository;
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private VotoRepository votoRepository;
    
    @Autowired
    private ControlVotoRepository controlVotoRepository;
    
    // Obtener elecciones activas
    @GetMapping("/elecciones")
    public Map<String, Object> getEleccionesActivas() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Eleccion> elecciones = eleccionRepository.findByActivaTrueOrderByFechaDesc();
            response.put("success", true);
            response.put("elecciones", elecciones);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener elecciones: " + e.getMessage());
        }
        
        return response;
    }
    
    // Verificar si un usuario puede votar
    @PostMapping("/verificar")
    public Map<String, Object> verificarPuedeVotar(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = request.get("cedula");
            Long eleccionId = Long.parseLong(request.get("eleccionId"));
            
            // Buscar usuario
            Optional<Usuario> usuario = usuarioRepository.findByCedula(cedula);
            if (!usuario.isPresent()) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return response;
            }
            
            // Buscar elección
            Optional<Eleccion> eleccion = eleccionRepository.findById(eleccionId);
            if (!eleccion.isPresent()) {
                response.put("success", false);
                response.put("message", "Elección no encontrada");
                return response;
            }
            
            // Verificar si ya votó
            boolean yaVoto = controlVotoRepository.existsByCiudadanoAndEleccion(usuario.get(), eleccion.get());
            
            if (yaVoto) {
                response.put("success", false);
                response.put("message", "Ya ha votado en esta elección");
                response.put("yaVoto", true);
            } else {
                response.put("success", true);
                response.put("message", "Puede votar");
                response.put("yaVoto", false);
                response.put("ciudadano", usuario.get().getNombre());
                response.put("eleccion", eleccion.get().getDescripcion());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al verificar: " + e.getMessage());
        }
        
        return response;
    }
    
    // Obtener listas disponibles para una elección
    @GetMapping("/listas/{eleccionId}")
    public Map<String, Object> getListasPorEleccion(@PathVariable Long eleccionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Lista> listas = listaRepository.findByEleccionIdAndActivaTrueOrderByNumero(eleccionId);
            response.put("success", true);
            response.put("listas", listas);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener listas: " + e.getMessage());
        }
        
        return response;
    }
    
    // Registrar voto
    @PostMapping("/votar")
    public Map<String, Object> registrarVoto(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = (String) request.get("cedula");
            Long eleccionId = Long.parseLong(request.get("eleccionId").toString());
            Long mesaId = Long.parseLong(request.get("mesaId").toString());
            Boolean votoEnBlanco = (Boolean) request.get("votoEnBlanco");
            Long listaId = null;
            
            if (!votoEnBlanco && request.get("listaId") != null) {
                listaId = Long.parseLong(request.get("listaId").toString());
            }
            
            // Verificaciones
            Optional<Usuario> usuario = usuarioRepository.findByCedula(cedula);
            Optional<Eleccion> eleccion = eleccionRepository.findById(eleccionId);
            Optional<Mesa> mesa = mesaRepository.findById(mesaId);
            
            if (!usuario.isPresent() || !eleccion.isPresent() || !mesa.isPresent()) {
                response.put("success", false);
                response.put("message", "Datos inválidos");
                return response;
            }
            
            // Verificar que no haya votado ya
            if (controlVotoRepository.existsByCiudadanoAndEleccion(usuario.get(), eleccion.get())) {
                response.put("success", false);
                response.put("message", "Ya ha votado en esta elección");
                return response;
            }
            
            // Registrar voto anónimo
            Voto voto = new Voto();
            voto.setMesa(mesa.get());
            voto.setEleccion(eleccion.get());
            voto.setVotoEnBlanco(votoEnBlanco);
            
            if (!votoEnBlanco && listaId != null) {
                Optional<Lista> lista = listaRepository.findById(listaId);
                if (lista.isPresent()) {
                    voto.setLista(lista.get());
                }
            }
            
            votoRepository.save(voto);
            
            // Registrar control de voto (con id del ciudadano)
            ControlVoto controlVoto = new ControlVoto(usuario.get(), eleccion.get(), mesa.get());
            controlVotoRepository.save(controlVoto);
            
            response.put("success", true);
            response.put("message", "Voto registrado exitosamente");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar voto: " + e.getMessage());
        }
        
        return response;
    }
    
    // Obtener resultados de una elección
    @GetMapping("/resultados/{eleccionId}")
    public Map<String, Object> getResultados(@PathVariable Long eleccionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Votos por lista
            List<Object[]> votosPorLista = votoRepository.contarVotosPorLista(eleccionId);
            
            // Votos en blanco
            Long votosEnBlanco = votoRepository.contarVotosEnBlanco(eleccionId);
            
            // Total de votos
            Long totalVotos = votoRepository.countByEleccionId(eleccionId);
            
            response.put("success", true);
            response.put("votosPorLista", votosPorLista);
            response.put("votosEnBlanco", votosEnBlanco);
            response.put("totalVotos", totalVotos);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener resultados: " + e.getMessage());
        }
        
        return response;
    }
}