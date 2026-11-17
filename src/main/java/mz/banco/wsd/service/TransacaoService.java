package mz.banco.wsd.service;

import mz.banco.wsd.entity.Conta;
import mz.banco.wsd.entity.Cliente;
import mz.banco.wsd.entity.Transacao;

import mz.banco.wsd.repository.ContaRepository;
import mz.banco.wsd.repository.ClienteRepository;
import mz.banco.wsd.repository.TransacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransacaoService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public TransacaoService(
            ContaRepository contaRepository,
            ClienteRepository clienteRepository,
            TransacaoRepository transacaoRepository,
            AuditoriaService auditoriaService) {

        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.transacaoRepository = transacaoRepository;
        this.auditoriaService = auditoriaService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // TRANSFERIR DINHEIRO
    
    @Transactional
    public Transacao transferir(
            String emailCliente,
            String numeroContaOrigem,
            String numeroContaDestino,
            String pin,
            BigDecimal valor) {

        // 1. IDENTIFICAR CLIENTE AUTENTICADO
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        // 2. VALIDAR NÚMEROS DAS CONTAS
        if (numeroContaOrigem == null || numeroContaOrigem.isBlank()
                || numeroContaDestino == null || numeroContaDestino.isBlank()) {

            throw new RuntimeException(
                    "Os números das contas são obrigatórios");
        }

        // 3. IMPEDIR TRANSFERÊNCIA PARA A MESMA CONTA
        if (numeroContaOrigem.equals(numeroContaDestino)) {

            throw new RuntimeException(
                    "Não é possível transferir para a mesma conta");
        }

        // 4. VALIDAR VALOR
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "O valor deve ser maior que zero");
        }

        // 5. BLOQUEAR AS DUAS CONTAS
        // A ordem evita bloqueios simultâneos em ordem inversa.

        Conta origem;
        Conta destino;

        if (numeroContaOrigem.compareTo(numeroContaDestino) < 0) {

            origem = contaRepository
                    .findByNumeroContaComBloqueio(numeroContaOrigem)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Conta de origem não encontrada"));

            destino = contaRepository
                    .findByNumeroContaComBloqueio(numeroContaDestino)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Conta de destino não encontrada"));

        } else {

            destino = contaRepository
                    .findByNumeroContaComBloqueio(numeroContaDestino)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Conta de destino não encontrada"));

            origem = contaRepository
                    .findByNumeroContaComBloqueio(numeroContaOrigem)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Conta de origem não encontrada"));
        }

        // 6. VERIFICAR TITULARIDADE DA CONTA DE ORIGEM
        if (!origem.getCliente().getId().equals(cliente.getId())) {

            throw new RuntimeException(
                    "Não tem autorização para transferir desta conta");
        }

        // 7. VERIFICAR ESTADO DAS CONTAS
        if (!"activa".equals(origem.getEstado())
                || !"activa".equals(destino.getEstado())) {

            throw new RuntimeException(
                    "As duas contas devem estar activas");
        }

        // 8. VALIDAR PIN
        if (pin == null || !pin.matches("\\d{4}")) {

            throw new RuntimeException(
                    "O PIN deve conter exactamente 4 dígitos");
        }

        if (!passwordEncoder.matches(pin, origem.getPin())) {

            throw new RuntimeException("PIN incorrecto");
        }

        // 9. VERIFICAR SALDO
        if (origem.getSaldo().compareTo(valor) < 0) {

            throw new RuntimeException("Saldo insuficiente");
        }

        // 10. ACTUALIZAR SALDOS
        origem.setSaldo(origem.getSaldo().subtract(valor));

        destino.setSaldo(destino.getSaldo().add(valor));

        contaRepository.save(origem);
        contaRepository.save(destino);

        // 11. REGISTAR TRANSACÇÃO
        Transacao transacao = new Transacao();

        transacao.setTipo("TRANSFERENCIA");
        transacao.setValor(valor);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);
        transacao.setEstado("CONCLUIDA");

        Transacao transacaoGuardada =
                transacaoRepository.save(transacao);

        // 12. REGISTAR AUDITORIA
        auditoriaService.registarAccao(
                null,
                origem.getCliente().getId(),
                "Transferência de " + valor.toPlainString()
                        + " MT da conta " + numeroContaOrigem
                        + " para a conta " + numeroContaDestino
        );

        return transacaoGuardada;
    }

    

    public List<Transacao> consultarHistorico(
            String emailCliente,
            String numeroConta) {

        // IDENTIFICAR CLIENTE AUTENTICADO
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        // LOCALIZAR CONTA
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        // VERIFICAR TITULARIDADE
        if (!conta.getCliente().getId().equals(cliente.getId())) {

            throw new RuntimeException(
                    "Não tem autorização para consultar esta conta");
        }

        // DEVOLVER HISTÓRICO
        return transacaoRepository.buscarHistoricoPorConta(conta.getId());
    }
}