package com.ecom.catalogueservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException() {
        super("Stock insuffisant");
    }
}
