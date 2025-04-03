package pessoas.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pessoas.Entity.Pessoa;
import pessoas.Excetions.GlobalExceptionHandler;
import pessoas.Excetions.NaoDadosFailedException;
import pessoas.Repository.PessoaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PropertySource("classpath:/application.properties")
@Service
public interface PessoaService {

    public List<Pessoa> findAll();

    public Optional<Pessoa> findById(UUID id);

    public Pessoa save(Pessoa pessoa);

    public void deleteById(UUID id);

    public Pessoa update(Pessoa pessoa);

    public ResponseEntity throwNoData() throws NaoDadosFailedException;
}
