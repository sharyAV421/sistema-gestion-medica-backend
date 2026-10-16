package com.gestionmedica.controller;

import com.gestionmedica.dto.ApiDtos.ErrorResponse;
import com.gestionmedica.exception.ApiException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class) public ResponseEntity<ErrorResponse> handle(ApiException e) { return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getMessage(),e.getStatus().value())); }
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) { String msg=e.getBindingResult().getFieldErrors().stream().map(x -> x.getField()+": valor inválido o requerido").findFirst().orElse("Solicitud inválida"); return ResponseEntity.badRequest().body(new ErrorResponse(msg,400)); }
    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class}) public ResponseEntity<ErrorResponse> malformed(Exception e) { return ResponseEntity.badRequest().body(new ErrorResponse("El cuerpo de la solicitud no es válido",400)); }
    @ExceptionHandler(Exception.class) public ResponseEntity<ErrorResponse> other(Exception e) { return ResponseEntity.status(500).body(new ErrorResponse("Error interno del servidor",500)); }
}
