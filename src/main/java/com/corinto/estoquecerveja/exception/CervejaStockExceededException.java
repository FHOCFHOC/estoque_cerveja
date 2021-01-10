package com.corinto.estoquecerveja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CervejaStockExceededException extends Exception {

    public CervejaStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Cerveja com %s ID excedeu a capacidade do estoque: %s", id, quantityToIncrement));
    }
}
