package pessoas.Service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import pessoas.Entity.Pessoa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PessoaServiceTest {
    private final Logger log = LoggerFactory.getLogger(PessoaServiceTest.class);

    @Autowired
    private PessoaService pessoaService;

    //    @InjectMocks
    //    Service
    List<UUID> listUuid = new ArrayList<>();
    UUID uuidGenerated;
    static UUID uuidProvideds;
    UUID uuidProvided;
    Pessoa pessoa;
    Pessoa pessoaProvided;

    @BeforeAll
    static void superSetup() {
        uuidProvideds = UUID.randomUUID();

    }

    @BeforeEach
    void setUp() {
        pessoaProvided = new Pessoa();
//        uuidProvided = UUID.randomUUID();
        uuidProvided = uuidProvideds;
        listUuid.add(uuidProvided);
//        u = UUID.fromString("00000000-5555-7777-1dcd-300020001001");
        pessoaProvided.setUuid(uuidProvided);
        pessoaProvided.setNome("anonimo " + uuidProvided.toString().substring(24));
        pessoaProvided.setEndereco("direccion" + uuidProvided.toString().substring(24));
    }

    @Test()
    @Order(1)
    void findAllEmpty() {
        System.out.println(pessoaService.findAll());
//        Assertions.assertTrue(pessoaService.findAll().isEmpty());
        log.atInfo().log(pessoaService.findAll().toString());
    }

    /*
    it is needed to set up generator annotations in Pessoa data class.
     */
    @Test
    @Order(2)
    void saveUuidServerGenerated() {
        pessoa = new Pessoa();
//        uuidPessoa = UUID.randomUUID();
//        u = UUID.fromString("00000000-5555-7777-1dcd-300020001001");
//        pessoa.setUuid(uuidGenerated);
        pessoa.setNome("Pepe ");
        pessoa.setEndereco("Direccion ");
        Pessoa pessoaSaved = pessoaService.save(pessoa);
        assertFalse(pessoaSaved.getUuid().toString().isEmpty());
        uuidGenerated = pessoaSaved.getUuid();
        log.info(uuidGenerated.toString());
        log.info(pessoaSaved.toString());

        addSomeCasesNoId();

    }

    /*
            Need to clean up generator annotations in Pessoa data class to work.
     */
//    @Test
    @Order(3)
    void saveUuidProvided() {

        pessoaProvided = pessoaService.save(pessoaProvided);
        assertFalse(pessoaProvided.getUuid().toString().isEmpty());

        addSomeCasesWithId();

        assertTrue(pessoaService.findAll().size() > 1);
        System.out.println(pessoaService.findAll().size());

        pessoaService.findAll().stream().forEach((p) -> System.out.println(p));
//        System.out.println(pessoaService.findAll());
        System.out.println();
        log.info(uuidProvided.toString());
        log.info(pessoaProvided.toString());
    }

    private void addSomeCasesNoId() {
        //      NOTE: This break the solid principle. This is one for explanations purpose.
        for (int i = 0; i < 4; i++) {
            listUuid.add(pessoaProvidedNoUUId(i).getUuid());
        }

    }
    private void addSomeCasesWithId() {
        //      NOTE: This break the solid principle. This is one for explanations purpose.
        for (int i = 0; i < 4; i++) {
            listUuid.add(pessoaProvided(i).getUuid());
        }

    }

    Pessoa pessoaProvided(int i) {
        pessoa = new Pessoa();
        uuidProvided = UUID.randomUUID();
        listUuid.add(uuidProvided);
//        u = UUID.fromString("00000000-5555-7777-1dcd-300020001001");
        pessoa.setUuid(uuidProvided);
        pessoa.setNome("Pepe " + i + " " + uuidProvided.toString().substring(24));
        pessoa.setEndereco("Address " + uuidProvided.toString().substring(24));
        return pessoaService.save(pessoa);

    }

    Pessoa pessoaProvidedNoUUId(int i) {
        pessoa = new Pessoa();
        pessoa.setNome("Pepe " + i + " " + uuidProvided.toString().substring(24));
        pessoa.setEndereco("Address " + uuidProvided.toString().substring(24));
        Pessoa pessoaSaved =pessoaService.save(pessoa);
        listUuid.add(pessoaSaved.getUuid());
        return pessoaSaved;

    }

    @Test
    @Order(4)
    void findById() {

        System.out.println("uuidProvided " + uuidProvided.toString());
        System.out.println(listUuid.size());
        System.out.println(pessoaService.findAll().size());
        System.out.println(pessoaService.findAll());
        System.out.println();

        pessoaService.findById(uuidProvided).ifPresent((p) -> {
            log.info(p.toString());
        });
    }

    @Test
    @Order(5)
    void update() {
//      Getting a pessoa that exist.
        Pessoa preUpdated;
        Pessoa postUpdated;
       uuidProvided=pessoaService.findAll().getLast().getUuid();  //jdk 23
//       uuidProvided=pessoaService.findAll().get(0).getUuid();  //jdk 17
        preUpdated = pessoaService.findById(uuidProvided).get();
        String oldAdd = preUpdated.getEndereco();
        log.info(oldAdd);

        String newAdd = "meu novo endereco";
        preUpdated.setEndereco(newAdd);

        postUpdated = pessoaService.save(preUpdated);
        log.info(postUpdated.toString());
        assertNotEquals(oldAdd, postUpdated.getEndereco().toString());

    }

    @Test
    @Order(6)
    void deleteById() {
//      Getting a pessoa that exist.
        uuidProvided=pessoaService.findAll().getLast().getUuid();   //jdk 23
//        uuidProvided=pessoaService.findAll().get(0).getUuid();   //jdk 17

        Pessoa preDeleted = pessoaService.findById(uuidProvided).get();
        log.info("uuid for deleting - " + uuidProvided.toString());
        pessoaService.deleteById(preDeleted.getUuid());
        pessoaService.findById(uuidProvided).ifPresentOrElse((p) -> {
            log.info(p.toString());
        }, () -> log.info("Successfully removed " + HttpStatus.NO_CONTENT));

    }

    @Test
    @Order(10)
    void findAllFullField() throws InterruptedException {

        System.out.println(pessoaService.findAll().size());
        System.out.println(pessoaService.findAll());
        System.out.println();
        log.info(pessoaService.findAll().toString());
    }


}