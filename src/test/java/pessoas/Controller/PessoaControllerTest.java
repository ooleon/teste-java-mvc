package pessoas.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pessoas.Entity.Pessoa;
import pessoas.Mapper.PessoaMapper;
import pessoas.Service.PessoaService;
import pessoas.dto.PessoaDTO;

import java.util.*;


//@SpringBootTest
@WebMvcTest(PessoaController.class)
@ExtendWith(SpringExtension.class)   // Extensión para integrar Spring con JUnit 5
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PessoaControllerTest {

    private static final Logger log = LoggerFactory.getLogger(PessoaControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PessoaService service;

    @InjectMocks
    private PessoaController controller;

    private ObjectMapper objectMapper;

    private Pessoa pessoa;
    private PessoaDTO pessoaDTO;

    private final UUID pessoaId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Arrange (Preparação, Preparación)
        pessoa = new Pessoa();
        pessoa.setNome("Jose");
        pessoa.setEndereco("Rua nessa, 123");
//        pessoaDTO = PessoaMapper.entityToDTO(pessoa);
    }

    /**
     * Initial test with DB empty
     */
//    @Test
    @Order(1)
    void testFindAllEmpty() throws Exception {
        log.info("inicio test testFindAllEmpty");
        System.out.println("inicio test testFindAllEmpty");
        when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pessoas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    //    @Test
    void testFindAll() throws Exception {
        List<Pessoa> pessoas = Arrays.asList(pessoa);
        when(service.findAll()).thenReturn(pessoas);

        mockMvc.perform(get("/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$[0].endereco").value(pessoa.getEndereco()));
    }


    //    @Test
    void testFindById() throws Exception {
        when(service.findById(any(UUID.class))).thenReturn(Optional.of(pessoa));

        mockMvc.perform(get("/pessoas/{id}", pessoa.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$.endereco").value(pessoa.getEndereco()));
    }

    @Test
    @Order(3)
    void testFindByIdNotFound() throws Exception {
        log.info("inicio test testFindByIdNotFound");
        when(service.findById(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/pessoas/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/pessoas/{id}", pessoaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("404 NOT_FOUND \"Pessoa não encontrada\""))

        ;

/*                .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(pessoaId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("João Silva"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endereco").value("Rua das Flores, 123"));*/


    }

    //    @Test
    @Order(2)
    void testCreate() throws Exception {
        when(service.save(any(Pessoa.class))).thenReturn(pessoa);

        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$.endereco").value(pessoa.getEndereco()));
    }

    //    @Test
    void testUpdate() throws Exception {
        when(service.update(any(Pessoa.class))).thenReturn(pessoa);

        mockMvc.perform(put("/pessoas/{id}", pessoa.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(pessoa.getNome()))
                .andExpect(jsonPath("$.endereco").value(pessoa.getEndereco()));
    }

    //    @Test
    void testPatch() throws Exception {
        when(service.findById(any(UUID.class))).thenReturn(Optional.of(pessoa));
        when(service.update(any(Pessoa.class))).thenReturn(pessoa);

        pessoaDTO.setNome("Novo Nome");

        mockMvc.perform(patch("/pessoas/{id}", pessoa.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"))
                .andExpect(jsonPath("$.endereco").value(pessoa.getEndereco()));
    }

    //    @Test
    void testDelete() throws Exception {
        doNothing().when(service).deleteById(any(UUID.class));

        mockMvc.perform(delete("/pessoas/{id}", pessoa.getUuid()))
                .andExpect(status().isNoContent());
    }
}