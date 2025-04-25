package com.edu.unbosque.service;

import com.edu.unbosque.repository.TraderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraderService {

    @Autowired
    private TraderRepository traderRepository;


    public boolean esTrader(int idUsuario) {
        return traderRepository.existsById(idUsuario);
    }


}