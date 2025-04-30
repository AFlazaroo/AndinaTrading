package com.edu.unbosque.service;

import com.edu.unbosque.model.Roles;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private  UsuarioRepository usuarioRepository;
    private  Roles roles;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean existeUsuario(int id) {
        return usuarioRepository.existsById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorId(Integer idUsuario) {
        try {
            Usuario usuario = usuarioRepository.encontrarUsuarioBasicoPorId(idUsuario);
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Usuario> guardarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isEmpty()){
            usuario.setRol(Roles.buscarRol(usuario.getRol()));
            return Optional.of(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }


}