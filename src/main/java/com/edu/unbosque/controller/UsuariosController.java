package com.edu.unbosque.controller;


import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearNotificacion(@RequestBody Usuario usuario) {
        Usuario usuarioResponse = usuarioService.CrearUsuario(usuario);
        return ResponseEntity.ok(usuarioResponse);
    }

}
