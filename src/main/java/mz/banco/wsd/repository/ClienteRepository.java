package mz.banco.wsd.repository;

import mz.banco.wsd.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNumeroBi(String numeroBi);

    Optional<Cliente> findByEmail(String email);

}
