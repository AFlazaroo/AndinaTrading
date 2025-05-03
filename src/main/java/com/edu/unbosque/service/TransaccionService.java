package com.edu.unbosque.service;

import com.edu.unbosque.model.Comisionista;
import com.edu.unbosque.model.Orden;
import com.edu.unbosque.model.Transaccion;
import com.edu.unbosque.model.Usuario_Comisionista;
import com.edu.unbosque.repository.TransaccionRepository;
import com.edu.unbosque.repository.UsuarioComisionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    private static final double PORCENTAJE_COMISION = 0.02;
    private static final double PORCENTAJE_SISTEMA = 0.6;
    private static final double PORCENTAJE_COMISIONISTA = 0.4;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private UsuarioComisionistaRepository usuarioComisionistaRepository;



    public Transaccion registrarTransaccion(Orden orden) {
        if (orden.getPrecio() == null) {
            throw new IllegalArgumentException("El precio de la orden no está disponible.");
        }

        double monto = orden.getPrecio() * orden.getCantidad();
        double comisionTotal = monto * PORCENTAJE_COMISION;

        // Verificamos si el usuario tiene comisionista
        boolean tieneComisionista = tieneComisionista(orden.getUsuario().getIdUsuario());

        double comisionSistema = tieneComisionista ? comisionTotal * PORCENTAJE_SISTEMA : comisionTotal;
        double comisionComisionista = tieneComisionista ? comisionTotal * PORCENTAJE_COMISIONISTA : 0.0;

        // Obtener los comisionistas asociados al usuario
        List<Usuario_Comisionista> comisionistas = usuarioComisionistaRepository.findByUsuario_IdUsuario(orden.getUsuario().getIdUsuario());

        // Inicializamos el objeto comisionista
        Comisionista comisionista = null;

        // Si el usuario tiene comisionista asociado, obtenemos el primero de la lista
        if (!comisionistas.isEmpty()) {
            comisionista = comisionistas.get(0).getComisionista();
        }

        // Crear la transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setMonto(monto);
        transaccion.setComision_total(comisionTotal);
        transaccion.setComision_sistema(comisionSistema);
        transaccion.setComision_comisionista(comisionComisionista);
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setOrden(orden);

        // Si existe un comisionista, asignamos ese comisionista a la transacción
        if (comisionista != null) {
            transaccion.setComisionista(comisionista);
        }

        // Guardamos la transacción en la base de datos
        return transaccionRepository.save(transaccion);
    }



    private boolean tieneComisionista(Integer idUsuario) {
        return !usuarioComisionistaRepository.findByUsuario_IdUsuario(idUsuario).isEmpty();
    }


}
