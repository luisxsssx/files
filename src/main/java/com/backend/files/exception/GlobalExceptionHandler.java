package com.backend.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFound(FileNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File Not Found");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex){
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An IO erro ocurred");
    }
}