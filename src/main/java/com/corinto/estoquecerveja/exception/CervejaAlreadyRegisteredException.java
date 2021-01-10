package com.corinto.estoquecerveja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CervejaAlreadyRegisteredException extends Exception{

    public CervejaAlreadyRegisteredException(String cervejaNome) {
        super(String.format("Cerveja com nome %s ja é registrada no sistema.", cervejaNome));
    }
}
