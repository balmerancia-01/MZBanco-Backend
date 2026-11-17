
package mz.banco.wsd.service;

import mz.banco.wsd.entity.Conta;
import mz.banco.wsd.entity.Cliente;
import mz.banco.wsd.entity.Transacao;
import mz.banco.wsd.entity.Auditoria;

import mz.banco.wsd.repository.ContaRepository;
import mz.banco.wsd.repository.ClienteRepository;
import mz.banco.wsd.repository.TransacaoRepository;
import mz.banco.wsd.repository.AuditoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ContaService(
            ContaRepository contaRepository,
            ClienteRepository clienteRepository,
            TransacaoRepository transacaoRepository,
            AuditoriaRepository auditoriaRepository) {

        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.transacaoRepository = transacaoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // IDENTIFICAR CLIENTE AUTENTICADO
    private Cliente obterCliente(String email) {

        return clienteRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Cliente autenticado não encontrado"));
    }

    // VERIFICAR TITULARIDADE DA CONTA
    private void verificarTitularidade(Conta conta, Cliente cliente) {

        if (!Objects.equals(
                conta.getCliente().getId(),
                cliente.getId())) {

            throw new RuntimeException(
                    "Acesso negado: esta conta não pertence ao cliente autenticado");
        }
    }

    // REGISTAR AUDITORIA
    private void registarAuditoria(Integer clienteId, String accao) {

        Auditoria auditoria = new Auditoria();

        auditoria.setIdCliente(clienteId);
        auditoria.setAccao(accao);
        auditoria.setDataHora(LocalDateTime.now());

        auditoriaRepository.save(auditoria);
    }

    // ABRIR CONTA
    @Transactional
    public Conta abrirConta(
            String email,
            Integer clienteId,
            String numeroConta,
            String pin,
            BigDecimal saldoInicial) {

        Cliente cliente = obterCliente(email);

        if (!Objects.equals(cliente.getId(), clienteId)) {
            throw new RuntimeException(
                    "Não pode abrir uma conta em nome de outro cliente");
        }

        if (pin == null || !pin.matches("\\d{4}")) {
            throw new RuntimeException(
                    "O PIN deve conter exactamente 4 dígitos");
        }

        if (saldoInicial == null ||
                saldoInicial.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "O saldo inicial não pode ser negativo");
        }

        if (numeroConta == null || numeroConta.isBlank()) {
            throw new RuntimeException(
                    "O número da conta é obrigatório");
        }

        if (contaRepository.findByNumeroConta(numeroConta).isPresent()) {
            throw new RuntimeException(
                    "Este número de conta já está registado");
        }

        Conta conta = new Conta();

        conta.setCliente(cliente);
        conta.setNumeroConta(numeroConta);
        conta.setPin(passwordEncoder.encode(pin));
        conta.setSaldo(saldoInicial);
        conta.setEstado("activa");
        conta.setDataAbertura(LocalDateTime.now());

        Conta contaGuardada = contaRepository.save(conta);

        registarAuditoria(
                cliente.getId(),
                "Abertura de conta: " + numeroConta);

        return contaGuardada;
    }

    // DEPOSITAR DINHEIRO
    @Transactional
    public Conta depositar(
            String email,
            String numeroConta,
            BigDecimal valor) {

        Cliente cliente = obterCliente(email);

        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "O valor do depósito deve ser maior que zero");
        }

        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        verificarTitularidade(conta, cliente);

        if (!conta.getEstado().equals("activa")) {
            throw new RuntimeException(
                    "Esta conta não está activa");
        }

        conta.setSaldo(conta.getSaldo().add(valor));

        contaRepository.save(conta);

        Transacao transacao = new Transacao();

        transacao.setTipo("DEPOSITO");
        transacao.setValor(valor);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setContaOrigem(null);
        transacao.setContaDestino(conta);
        transacao.setEstado("CONCLUIDA");

        transacaoRepository.save(transacao);

        registarAuditoria(
                cliente.getId(),
                "Depósito de " + valor + " MT na conta " + numeroConta);

        return conta;
    }

    // CONSULTAR SALDO
    public Conta consultarSaldo(
            String email,
            String numeroConta) {

        Cliente cliente = obterCliente(email);

        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        verificarTitularidade(conta, cliente);

        if (!conta.getEstado().equals("activa")) {
            throw new RuntimeException(
                    "Esta conta não está activa");
        }

        return conta;
    }

    // BLOQUEAR CONTA
    @Transactional
    public Conta bloquearConta(
            String email,
            String numeroConta) {

        Cliente cliente = obterCliente(email);

        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        verificarTitularidade(conta, cliente);

        if (conta.getEstado().equals("bloqueada")) {
            throw new RuntimeException(
                    "Esta conta já está bloqueada");
        }

        conta.setEstado("bloqueada");

        Conta contaGuardada = contaRepository.save(conta);

        registarAuditoria(
                cliente.getId(),
                "Bloqueio da conta: " + numeroConta);

        return contaGuardada;
    }

    // DESBLOQUEAR CONTA
    @Transactional
    public Conta desbloquearConta(
            String email,
            String numeroConta) {

        Cliente cliente = obterCliente(email);

        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() ->
                        new RuntimeException("Conta não encontrada"));

        verificarTitularidade(conta, cliente);

        if (conta.getEstado().equals("activa")) {
            throw new RuntimeException(
                    "Esta conta já está activa");
        }

        conta.setEstado("activa");

        Conta contaGuardada = contaRepository.save(conta);

        registarAuditoria(
                cliente.getId(),
                "Desbloqueio da conta: " + numeroConta);

        return contaGuardada;
    }
}