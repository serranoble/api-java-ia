package com.qa.practice.products.exception;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String gtin) {
        super("Ya existe un producto registrado con GTIN: " + gtin);
    }
}
