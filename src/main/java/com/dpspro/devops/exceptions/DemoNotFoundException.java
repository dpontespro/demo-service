package com.dpspro.devops.exceptions;

import org.springframework.http.HttpStatus;

public class DemoNotFoundException extends RuntimeException {
    private static final String DESCRIPTION = "Demo Not Found Exception";
    public DemoNotFoundException(HttpStatus notFound, String detail) {

        super(DESCRIPTION + ". " + detail);
    }

    public DemoNotFoundException() {
    }
}
