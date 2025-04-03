package pessoas.Excetions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pessoas.Service.PessoaServiceImpl;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleExceptionPNEFBySpring(ResponseStatusException e) {
        log.warn("ResponseStatusException " + e.getReason());
        // log exception
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(PessoaNaoEncontradaFailedException.class)
    public ResponseEntity handleExceptionPNEFByCustomClass(PessoaNaoEncontradaFailedException e) {
        log.error("PessoaNaoEncontradaFailedException " + e.getMessage());
        // log exception
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(NaoDadosFailedException.class)
    public ResponseEntity handleExceptionNDF(NaoDadosFailedException e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(e.getMessage());
    }
}