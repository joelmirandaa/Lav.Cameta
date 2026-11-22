package br.com.lavicameta.exception;

import br.com.lavicameta.dto.ErroResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> tratarApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ErroResponse.de(ex.getCodigo(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getObjectName(), erro.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErroResponse(
                "DADOS_INVALIDOS", "Verifique os dados informados.", campos, Instant.now()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResponse> tratarRequisicaoMalformada(Exception ex) {
        return ResponseEntity.badRequest().body(ErroResponse.de("REQUISICAO_INVALIDA", "Requisição inválida."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErroResponse.de("ACESSO_NEGADO", "Você não tem permissão para esta ação."));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, PessimisticLockingFailureException.class})
    public ResponseEntity<ErroResponse> tratarConflito(Exception ex) {
        LOG.warn("Conflito de persistência: {}", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResponse.de("CONFLITO", "A operação conflita com outra em andamento. Tente novamente."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMetodo(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErroResponse.de("METODO_NAO_SUPORTADO", "Método não suportado."));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMidia(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErroResponse.de("MIDIA_NAO_SUPORTADA", "Envie os dados em JSON."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> tratarRecursoInexistente(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.de("NAO_ENCONTRADO", "Recurso não encontrado."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarInesperado(Exception ex) {
        LOG.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.de("ERRO_INTERNO", "Não foi possível concluir a operação. Tente novamente."));
    }
}
