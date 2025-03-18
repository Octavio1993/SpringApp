package com.example.springapp.app.spring.exception;

public class CustomeFieldValidationException extends Exception{
    private String fieldName;

    public CustomeFieldValidationException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}
