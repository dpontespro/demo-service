package com.dpontespro.devops.exceptions;

import org.springframework.http.HttpStatus;

public class DpsProDemoNotFoundException extends RuntimeException {
    private static final String DESCRIPTION = "DpsProDemo Not Found Exception";
    public DpsProDemoNotFoundException(HttpStatus notFound, String detail) {

        super(DESCRIPTION + ". " + detail);
    }

    public DpsProDemoNotFoundException() {
    }
}
