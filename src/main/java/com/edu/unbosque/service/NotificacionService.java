package com.edu.unbosque.service;


import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {
/*
    @Autowired
    private JavaMailSender mailSender;

    public void enviarNotificacion(String canal, String destino, String mensaje) {
        switch (canal.toLowerCase()) {
            case "email":
                enviarEmail(destino, mensaje);
                break;
            case "sms":
                enviarSms(destino, mensaje);
                break;
            case "whatsapp":
                enviarWhatsApp(destino, mensaje);
                break;
            default:
                throw new IllegalArgumentException("Canal de notificación no válido: " + canal);
        }
    }

    // Enviar notificación por Email
    private void enviarEmail(String destino, String mensaje) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(destino); // Dirección de correo electrónico
            helper.setSubject("Notificación importante");
            helper.setText(mensaje); // Mensaje de la notificación

            mailSender.send(message); // Enviar el correo
            System.out.println("Correo enviado a: " + destino);
        } catch (Exception e) {
            e.printStackTrace();
            // Aquí podrías manejar el error de forma más detallada
        }
    }

    // Enviar notificación por SMS (simulado)
    private void enviarSms(String destino, String mensaje) {
        // Simulamos el envío de un SMS (esto debería integrarse con un servicio real como Twilio)
        System.out.println("Enviando SMS a " + destino + ": " + mensaje);
    }

    // Enviar notificación por WhatsApp (simulado)
    private void enviarWhatsApp(String destino, String mensaje) {
        // Simulamos el envío de un WhatsApp (esto debería integrarse con un servicio real como WhatsApp Business API)
        System.out.println("Enviando WhatsApp a " + destino + ": " + mensaje);*/


    }


