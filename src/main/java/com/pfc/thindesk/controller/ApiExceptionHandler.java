package com.pfc.thindesk.controller;

import com.pfc.thindesk.service.ChamadoNaoEncontradoException;
import com.pfc.thindesk.service.RegraChamadoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(assignableTypes = ChamadoController.class)
public class ApiExceptionHandler {

    @ExceptionHandler(ChamadoNaoEncontradoException.class)
    public ResponseEntity<ApiErro> chamadoNaoEncontrado(
            ChamadoNaoEncontradoException exception, HttpServletRequest request) {
        return erro(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(RegraChamadoException.class)
    public ResponseEntity<ApiErro> conflitoDeRegra(
            RegraChamadoException exception, HttpServletRequest request) {
        return erro(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErro> acessoNegado(HttpServletRequest request) {
        return erro(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este chamado.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErro> requisicaoInvalida(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String mensagem = fieldError == null ? "Os dados enviados são inválidos." : fieldError.getDefaultMessage();
        return erro(HttpStatus.BAD_REQUEST, mensagem, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErro> jsonInvalido(HttpServletRequest request) {
        return erro(HttpStatus.BAD_REQUEST, "O corpo da requisição deve conter um JSON válido.", request);
    }

    private ResponseEntity<ApiErro> erro(HttpStatus status, String mensagem, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ApiErro(Instant.now(), status.value(), mensagem, request.getRequestURI()));
    }

    public record ApiErro(Instant dataHora, int status, String mensagem, String caminho) {
    }
}
