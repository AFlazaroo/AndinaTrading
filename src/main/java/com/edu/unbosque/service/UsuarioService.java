package com.edu.unbosque.service;

import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {


    private UsuarioRepository usuarioRepository;

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

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getNombre())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

}