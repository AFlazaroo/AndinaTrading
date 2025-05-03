package com.edu.unbosque.service;

import com.edu.unbosque.config.AppConfig;
import com.edu.unbosque.model.Roles;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {


    private  UsuarioRepository usuarioRepository;

    @Autowired
    private AppConfig appConfig;

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

    /**
     *
     * @param usuario
     * @return Regresa la entidad si le es posible guardarla , se cambiara a un boolean por comodidad en el siguiente
     * pull request
     */
    public Optional<Usuario> guardarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isEmpty()){
            usuario.setRol(Roles.buscarRol(usuario.getRol()));
            return Optional.of(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }

    /**
     *
     * Evitar Borrar este metodo se modificara para que las contrasenas se encripente en SHA256 correctamente
     * ATT Andres Cuta
     *
     */
//    public boolean validarCredenciales(String correo, String contrasena) {
//        Optional<Usuario> userOpt = usuarioRepository.findByEmail(correo);
//        if (userOpt.isPresent()) {
//            Usuario user = userOpt.get();
//            return appConfig.passwordEncoder().matches(contrasena,user.getPassword());
//        }
//        return false;
//    }

    /**
     *
     * @param correo
     * @param contrasena
     * @return regresa true o false dependiendo si existe el usuario y sus credenciales son correctas
     */
    public boolean validarCredenciales(String correo, String contrasena) {
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(correo);
        if (userOpt.isPresent()) {
            Usuario user = userOpt.get();
            return contrasena.equals(user.getPassword());
        }
        return false;
    }

    public Optional<Usuario> encontrarUsuarioCorreo(String correo) {
            return usuarioRepository.findByEmail(correo);
    }


}