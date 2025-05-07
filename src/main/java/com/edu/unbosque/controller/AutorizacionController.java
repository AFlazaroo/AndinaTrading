package com.edu.unbosque.controller;

import com.edu.unbosque.config.TokenAdmin;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.service.CorreosService;
import com.edu.unbosque.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AutorizacionController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CorreosService correosService;

    @Autowired
    private TokenAdmin tokenAdmin;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> jsonParametros) {
        String email = jsonParametros.get("email");
        String contrasena = jsonParametros.get("contrasena");

        if (usuarioService.validarCredenciales(email, contrasena)) {
            Optional<Usuario> usuarioOpt = usuarioService.encontrarUsuarioCorreo(email);
            if (usuarioOpt.isPresent()) {

                String codigoEnviado = String.valueOf(new Random().nextInt(900000) + 100000);
                otpStorage.put(email, codigoEnviado);
                correosService.sendOtpEmail(email, codigoEnviado);

                return ResponseEntity.ok("Código enviado al correo");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
    }

    @PostMapping("/mfa/verificar")
    public ResponseEntity<String> verificarOtp(@RequestBody Map<String, String> jsonParametros) {
        String email = jsonParametros.get("email");
        String codigoOtp = jsonParametros.get("codigoOtp");

        String storedOtp = otpStorage.get(email);

        if (storedOtp != null && storedOtp.equals(codigoOtp)) {
            otpStorage.remove(email);

            Optional<Usuario> usuarioOpt = usuarioService.encontrarUsuarioCorreo(email);
            if (usuarioOpt.isPresent()) {
                String id = usuarioOpt.get().getIdUsuario().toString();
                String token = tokenAdmin.generarToken(id);

                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código incorrecto.");
        }
    }
}
