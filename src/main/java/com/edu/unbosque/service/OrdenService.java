package com.edu.unbosque.service;


import com.edu.unbosque.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

}
