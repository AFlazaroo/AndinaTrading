package com.edu.unbosque.controller;


import com.edu.unbosque.model.Transaccion;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.TransaccionRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public TransaccionController(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Endpoint para obtener todas las transacciones de un usuario por ID
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerTransaccionesPorUsuario(@PathVariable Integer idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado con ID: " + idUsuario);
        }

        List<Transaccion> transacciones = transaccionRepository.findByOrden_Usuario_IdUsuario(idUsuario);

        return ResponseEntity.ok(transacciones);
    }
}

