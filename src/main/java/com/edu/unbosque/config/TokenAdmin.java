package com.edu.unbosque.config;
import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenAdmin {

    @Value("${jwt.secret}")
    private String llave;


    /**
     *
     * Genera el token que funcionara como la sesion de usuario
     *
     * @param identificadorUsuario
     * @return Genera un token
     */
    public String generarToken(String identificadorUsuario) {
        return Jwts.builder()
                .setSubject(identificadorUsuario)
                .signWith(SignatureAlgorithm.HS256, llave)
                .compact();
    }

    /**
     *
     * Esto valida el token creado y devuelve el id del usuario para poder ser buscado
     *
     * @Author Andres Cuta
     * @param token
     * @return
     */
    public String validarTokenIdentificadorUsuario(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(llave)
                    .parseClaimsJws(token)
                    .getBody();
            return claims
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
