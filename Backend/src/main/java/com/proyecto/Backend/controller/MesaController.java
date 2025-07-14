package com.proyecto.Backend.controller;

import com.proyecto.Backend.entity.Mesa;
import com.proyecto.Backend.entity.Usuario;
import com.proyecto.Backend.repository.MesaRepository;
import com.proyecto.Backend.repository.UsuarioRepository;
import com.proyecto.Backend.repository.VotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:3000")
public class MesaController {
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private VotoRepository votoRepository;
    
    // Obtener todas las mesas
    @GetMapping
    public ResponseEntity<List<Mesa>> obtenerTodasLasMesas() {
        try {
            List<Mesa> mesas = mesaRepository.findAll();
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener mesas abiertas
    @GetMapping("/abiertas")
    public ResponseEntity<List<Mesa>> obtenerMesasAbiertas() {
        try {
            List<Mesa> mesas = mesaRepository.findMesasAbiertas();
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener mesas cerradas
    @GetMapping("/cerradas")
    public ResponseEntity<List<Mesa>> obtenerMesasCerradas() {
        try {
            List<Mesa> mesas = mesaRepository.findMesasCerradas();
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Obtener mesas por presidente
    @GetMapping("/presidente/{cedula}")
    public ResponseEntity<List<Mesa>> obtenerMesasPorPresidente(@PathVariable String cedula) {
        try {
            // Verificar que es presidente
            Optional<Usuario> presidente = usuarioRepository.findPresidenteByCedula(cedula);
            if (presidente.isEmpty()) {
                // CORREGIDO: Usar status() en lugar de forbidden()
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<Mesa> mesas = mesaRepository.findMesasByPresidente(cedula);
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // CERRAR MESA - Función principal nueva
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<Map<String, String>> cerrarMesa(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String cedulaPresidente = request.get("cedulaPresidente");
            String motivo = request.get("motivo");
            
            if (cedulaPresidente == null || cedulaPresidente.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La cédula del presidente es requerida"));
            }
            
            if (motivo == null || motivo.trim().isEmpty()) {
                motivo = "Cierre normal de votación";
            }
            
            // Verificar que puede cerrar esta mesa
            if (!mesaRepository.puedeSerCerradaPor(id, cedulaPresidente)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Solo el presidente asignado puede cerrar esta mesa"));
            }
            
            Optional<Mesa> mesaOpt = mesaRepository.findById(id);
            if (mesaOpt.isEmpty()) {
                // CORREGIDO: Usar status() en lugar de notFound()
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            Mesa mesa = mesaOpt.get();
            if (mesa.getCerrada()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La mesa ya está cerrada"));
            }
            
            // Contar votos emitidos
            Integer totalVotos = votoRepository.countByMesaId(id);
            
            // Cerrar la mesa
            mesa.cerrarMesa(cedulaPresidente, motivo, totalVotos);
            mesaRepository.save(mesa);
            
            return ResponseEntity.ok()
                .body(Map.of("mensaje", "Mesa cerrada exitosamente. Total de votos: " + totalVotos));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }
    
    // Verificar estado de una mesa
    @GetMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> verificarEstadoMesa(@PathVariable Long id) {
        try {
            Optional<Mesa> mesaOpt = mesaRepository.findById(id);
            if (mesaOpt.isEmpty()) {
                // CORREGIDO: Usar status() en lugar de notFound()
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            Mesa mesa = mesaOpt.get();
            Boolean cerrada = mesaRepository.isCerrada(id);
            boolean abierta = cerrada == null || !cerrada;
            
            Map<String, Object> estado = Map.of(
                "id", mesa.getId(),
                "numero", mesa.getNumero(),
                "abierta", abierta,
                "totalVotos", mesa.getTotalVotosEmitidos() != null ? mesa.getTotalVotosEmitidos() : 0,
                "presidente", mesa.getPresidenteNombre() != null ? mesa.getPresidenteNombre() : "No asignado"
            );
            
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}