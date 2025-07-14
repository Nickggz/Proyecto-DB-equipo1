package com.proyecto.Backend.controller;

import com.proyecto.Backend.entity.*;
import com.proyecto.Backend.repository.*;
import com.proyecto.Backend.service.CircuitoValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private VotoRepository votoRepository;
    
    @Autowired
    private ControlVotoRepository controlVotoRepository;
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private CircuitoValidationService circuitoValidationService;

    @GetMapping("/elecciones")
    public ResponseEntity<Map<String, Object>> getElecciones() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Eleccion> elecciones = eleccionRepository.findByActivaTrue();
            
            response.put("success", true);
            response.put("elecciones", elecciones);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cargar elecciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificarVoto(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = (String) data.get("cedula");
            String credencialCivica = (String) data.get("credencialCivica");
            String eleccionIdStr = (String) data.get("eleccionId");
            
            if (cedula == null || eleccionIdStr == null) {
                response.put("success", false);
                response.put("message", "Datos incompletos");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long eleccionId = Long.parseLong(eleccionIdStr);
            
            // Verificar que el usuario existe
            Usuario usuario = usuarioRepository.findByCedula(cedula);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.ok(response);
            }
            
            // Si se proporciona credencial, verificarla
            if (credencialCivica != null && !credencialCivica.trim().isEmpty()) {
                if (!credencialCivica.trim().equals(usuario.getCredencialCivica())) {
                    response.put("success", false);
                    response.put("message", "Credencial cívica incorrecta");
                    return ResponseEntity.ok(response);
                }
            }

            // Verificar elección
            Eleccion eleccion = eleccionRepository.findById(eleccionId).orElse(null);
            if (eleccion == null) {
                response.put("success", false);
                response.put("message", "Elección no encontrada");
                return ResponseEntity.ok(response);
            }

            // Verificar si ya votó usando tu método existente
            boolean yaVoto = controlVotoRepository.existsByCiudadanoAndEleccion(usuario, eleccion);
            if (yaVoto) {
                response.put("success", false);
                response.put("yaVoto", true);
                response.put("message", "Ya has votado en esta elección");
                return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("yaVoto", false);
            response.put("message", "Puede votar");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al verificar voto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/listas/{eleccionId}")
    public ResponseEntity<Map<String, Object>> getListasPorEleccion(@PathVariable Long eleccionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Usar findByEleccionId en lugar de findByEleccionIdAndActivaTrue
            List<Lista> todasLasListas = listaRepository.findByEleccionId(eleccionId);
            
            // Filtrar las activas manualmente
            List<Lista> listas = todasLasListas.stream()
                .filter(Lista::getActiva)
                .collect(java.util.stream.Collectors.toList());
            
            response.put("success", true);
            response.put("listas", listas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cargar listas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/votar")
    public ResponseEntity<Map<String, Object>> registrarVoto(@RequestBody Map<String, Object> votoData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String cedula = (String) votoData.get("cedula");
            String credencialCivica = (String) votoData.get("credencialCivica");
            Long eleccionId = Long.parseLong(votoData.get("eleccionId").toString());
            Long mesaId = votoData.get("mesaId") != null ? Long.parseLong(votoData.get("mesaId").toString()) : 1L;
            Boolean votoEnBlanco = (Boolean) votoData.get("votoEnBlanco");
            Long listaId = votoData.get("listaId") != null ? Long.parseLong(votoData.get("listaId").toString()) : null;
            
            // Validar datos básicos
            if (cedula == null) {
                response.put("success", false);
                response.put("message", "Cédula es requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Verificar usuario
            Usuario usuario = usuarioRepository.findByCedula(cedula);
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.ok(response);
            }
            
            // Verificar credencial si se proporciona
            if (credencialCivica != null && !credencialCivica.trim().isEmpty()) {
                if (!credencialCivica.trim().equals(usuario.getCredencialCivica())) {
                    response.put("success", false);
                    response.put("message", "Credencial cívica incorrecta");
                    return ResponseEntity.ok(response);
                }
            }

            // Verificar elección
            Eleccion eleccion = eleccionRepository.findById(eleccionId).orElse(null);
            if (eleccion == null || !eleccion.getActiva()) {
                response.put("success", false);
                response.put("message", "Elección no encontrada o inactiva");
                return ResponseEntity.ok(response);
            }

            // Verificar si ya votó usando tu método existente
            boolean yaVoto = controlVotoRepository.existsByCiudadanoAndEleccion(usuario, eleccion);
            if (yaVoto) {
                response.put("success", false);
                response.put("message", "Ya has votado en esta elección");
                return ResponseEntity.ok(response);
            }

            // Determinar tipo de voto si tiene credencial cívica
            String tipoVoto = "VALIDO";
            if (credencialCivica != null && usuario.getCircuito() != null) {
                // Obtener mesa correcta según credencial
                Long mesaCorrecta = circuitoValidationService.obtenerMesaPorCredencial(credencialCivica);
                if (!mesaId.equals(mesaCorrecta)) {
                    tipoVoto = "OBSERVADO";
                }
            }

            // Verificar lista si no es voto en blanco
            Lista lista = null;
            if (!votoEnBlanco && listaId != null) {
                lista = listaRepository.findById(listaId).orElse(null);
                if (lista == null || !lista.getActiva()) {
                    response.put("success", false);
                    response.put("message", "Lista no encontrada o inactiva");
                    return ResponseEntity.ok(response);
                }
            }

            // Obtener mesa
            Mesa mesa = mesaRepository.findById(mesaId).orElse(null);
            if (mesa == null) {
                response.put("success", false);
                response.put("message", "Mesa electoral no encontrada");
                return ResponseEntity.ok(response);
            }

            // Crear el voto ANÓNIMO (sin información del votante)
            Voto voto = new Voto();
            voto.setEleccion(eleccion);
            voto.setLista(lista);
            voto.setMesa(mesa);
            voto.setFechaHora(LocalDateTime.now());
            voto.setVotoEnBlanco(votoEnBlanco);
            
            // NO guardar cédula ni credencial para mantener anonimato
            // voto.setCedulaVotante(cedula);  ← ELIMINADO para anonimato
            // voto.setCredencialVotante(credencialCivica);  ← ELIMINADO para anonimato
            
            // Establecer tipo de voto usando método correcto
            if ("OBSERVADO".equals(tipoVoto)) {
                voto.setObservaciones("Voto emitido fuera del circuito asignado");
                // Agregar lógica adicional para votos observados si es necesario
            }

            // Guardar el voto ANÓNIMO
            votoRepository.save(voto);

            // Crear control de voto SOLO para evitar doble votación (separado del voto)
            ControlVoto controlVoto = new ControlVoto();
            controlVoto.setCiudadano(usuario);  // Solo para control, no para el voto
            controlVoto.setEleccion(eleccion);
            controlVoto.setMesa(mesa);
            
            controlVotoRepository.save(controlVoto);

            // Preparar respuesta
            response.put("success", true);
            response.put("tipoVoto", tipoVoto);
            
            if ("VALIDO".equals(tipoVoto)) {
                response.put("message", "¡Voto registrado exitosamente!");
            } else {
                response.put("message", "Voto registrado como OBSERVADO");
            }
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error al registrar voto: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error interno al registrar voto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/validar-circuito")
    public ResponseEntity<Map<String, Object>> validarCircuito(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String credencialCivica = (String) data.get("credencialCivica");
            Long circuitoVotacionId = Long.parseLong(data.get("circuitoVotacionId").toString());
            
            if (credencialCivica == null) {
                response.put("success", false);
                response.put("message", "Credencial cívica requerida");
                return ResponseEntity.badRequest().body(response);
            }
            
            String tipoVoto = circuitoValidationService.obtenerTipoVoto(credencialCivica, circuitoVotacionId);
            
            response.put("success", true);
            response.put("tipoVoto", tipoVoto);
            
            if ("VALIDO".equals(tipoVoto)) {
                response.put("message", "Circuito correcto - Voto será válido");
            } else {
                response.put("message", "Circuito incorrecto - Voto será observado");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al validar circuito");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}