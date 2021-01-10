package com.corinto.estoquecerveja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CervejaNotFoundException extends Exception {

    public CervejaNotFoundException(String cervejaNome) {
        super(String.format("Cerveja com nome %s não foi encontrada no sistema.", cervejaNome));
    }

    public CervejaNotFoundException(Long id) {
        super(String.format("Cerveja com id %s não foi encontrada no sistema.", id));
    }
}
