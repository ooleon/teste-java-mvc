package pessoas.Controller;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pessoas.Entity.Pessoa;
import pessoas.Excetions.NaoDadosFailedException;
import pessoas.Excetions.PessoaNaoEncontrada;
import pessoas.Mapper.PessoaMapper;
import pessoas.Service.PessoaService;
import pessoas.dto.PessoaDTO;
import pessoas.Excetions.PessoaNaoEncontradaFailedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {
    private final Logger log = LoggerFactory.getLogger(PessoaController.class);
    private final PessoaService pessoaService;

    public PessoaController(PessoaService service) {
        this.pessoaService = service;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<?> findAll() throws NaoDadosFailedException {
        List<Pessoa> p;
        p = pessoaService.findAll();
        log.info(p.toString());

        //validate
//        if (p == null || p.isEmpty()) return ResponseEntity.noContent().build();
        if (p != null && !p.isEmpty()) return ResponseEntity.ok(p);

        return pessoaService.throwNoData();

//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }


    //    @PreAuthorize("hasRole('USER')") TODO: Search Spring Security dependence and implementation
    //    @Cacheable(value = "pessoas", key = "#uuid") TODO: Cacheable
    //    @Operation(summary = "Busca uma pessoa pelo ID")
    //    @ApiResponses(value = {
    //            @ApiResponse(responseCode = "200", description = "Pessoa encontrada"),
    //            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    //    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") @NotNull UUID uuid) {
        log.info("Buscando pessoa com ID: {}", uuid);
        // Busca a pessoa no repositório
/*
        Pessoa pessoa = pessoaService.findById(uuid)
                .orElseThrow(() -> {
                            log.warn("Pessoa com ID {} não encontrada", uuid);
                            PessoaNaoEncontrada pessoaNaoEncontrada = new PessoaNaoEncontrada();
//                    return new ResponseStatusException(HttpStatus.NOT_FOUND, pessoaNaoEncontrada.MESSAGE);
                    return new PessoaNaoEncontradaFailedException(pessoaNaoEncontrada.MESSAGE);
                        }
                );

        // Retorna a pessoa com status HTTP 200 (OK)
        return ResponseEntity.ok(pessoa);
*/

        Optional<Pessoa> pessoa = pessoaService.findById(uuid);

        if (pessoa == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        if (pessoa.isEmpty()) {
            PessoaNaoEncontrada pessoaNaoEncontrada = new PessoaNaoEncontrada();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pessoaNaoEncontrada);
        }

        PessoaDTO pessoaDTO = pessoa.map(p1 -> PessoaMapper.entityToDTO(p1)).get();
        return ResponseEntity.ok(pessoaDTO);

    }

    @PostMapping(value = {"", "/"})
    public Pessoa create(@RequestBody Pessoa pessoa) {
        log.info(pessoa.toString());
        return pessoaService.save(pessoa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaDTO> update(@PathVariable UUID uuid, @RequestBody Pessoa pessoa) {
        pessoa.setUuid(uuid);
        return ResponseEntity.ok(PessoaMapper.entityToDTO(pessoaService.update(pessoa)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PessoaDTO> patch(@PathVariable UUID id, @RequestBody PessoaDTO pessoaDTO) {
        Optional<Pessoa> existingPessoa = pessoaService.findById(id);
        if (existingPessoa.isPresent()) {
            Pessoa updatedPessoa = existingPessoa.get();
            if (pessoaDTO.getNome() != null) updatedPessoa.setNome(pessoaDTO.getNome());
            if (pessoaDTO.getEndereco() != null) updatedPessoa.setEndereco(pessoaDTO.getEndereco());
            return ResponseEntity.ok(PessoaMapper.entityToDTO(pessoaService.update(updatedPessoa)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pessoaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

