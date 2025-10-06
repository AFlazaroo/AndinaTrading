package co.edu.unbosque.microservice.usuario.model;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioComisionistaId implements Serializable {
    private Integer usuario;
    private Integer comisionista;

    public UsuarioComisionistaId() {}

    public UsuarioComisionistaId(Integer usuario, Integer comisionista) {
        this.usuario = usuario;
        this.comisionista = comisionista;
    }

}
