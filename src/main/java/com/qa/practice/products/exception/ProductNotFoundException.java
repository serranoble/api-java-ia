package com.qa.practice.products.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String gtin) {
        super("No se encontró ningún producto con GTIN: " + gtin);
    }
}
