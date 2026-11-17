package mz.banco.wsd.repository;

import mz.banco.wsd.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Integer> {

    Optional<Conta> findByNumeroConta(String numeroConta);

    @Query(
        value = "SELECT * FROM conta WHERE numero_conta = :numeroConta FOR UPDATE",
        nativeQuery = true
    )
    Optional<Conta> findByNumeroContaComBloqueio(
        @Param("numeroConta") String numeroConta
    );
}