
package mz.banco.wsd.controller;

import mz.banco.wsd.dto.AbrirContaRequest;
import mz.banco.wsd.dto.ContaResponse;
import mz.banco.wsd.dto.DepositoRequest;
import mz.banco.wsd.entity.Conta;
import mz.banco.wsd.service.ContaService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/contas")
@CrossOrigin(origins = "*")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // ABRIR CONTA
    @PostMapping
    public ResponseEntity<ContaResponse> abrirConta(
            @RequestBody AbrirContaRequest request,
            Authentication authentication) {

        Conta conta = contaService.abrirConta(
                authentication.getName(),
                request.getClienteId(),
                request.getNumeroConta(),
                request.getPin(),
                request.getSaldoInicial()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(converterResposta(conta));
    }

    // DEPOSITAR DINHEIRO
    @PostMapping("/depositar")
    public ResponseEntity<ContaResponse> depositar(
            @RequestBody DepositoRequest request,
            Authentication authentication) {

        Conta conta = contaService.depositar(
                authentication.getName(),
                request.getNumeroConta(),
                request.getValor()
        );

        return ResponseEntity.ok(converterResposta(conta));
    }

    // CONSULTAR SALDO
    @GetMapping("/saldo/{numeroConta}")
    public ResponseEntity<ContaResponse> consultarSaldo(
            @PathVariable String numeroConta,
            Authentication authentication) {

        Conta conta = contaService.consultarSaldo(
                authentication.getName(),
                numeroConta
        );

        return ResponseEntity.ok(converterResposta(conta));
    }

    // BLOQUEAR CONTA
    @PutMapping("/bloquear/{numeroConta}")
    public ResponseEntity<ContaResponse> bloquearConta(
            @PathVariable String numeroConta,
            Authentication authentication) {

        Conta conta = contaService.bloquearConta(
                authentication.getName(),
                numeroConta
        );

        return ResponseEntity.ok(converterResposta(conta));
    }

    // DESBLOQUEAR CONTA
    @PutMapping("/desbloquear/{numeroConta}")
    public ResponseEntity<ContaResponse> desbloquearConta(
            @PathVariable String numeroConta,
            Authentication authentication) {

        Conta conta = contaService.desbloquearConta(
                authentication.getName(),
                numeroConta
        );

        return ResponseEntity.ok(converterResposta(conta));
    }

    // CONVERTER CONTA EM RESPOSTA
    private ContaResponse converterResposta(Conta conta) {

        return new ContaResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getCliente().getNome(),
                conta.getSaldo(),
                conta.getEstado(),
                conta.getDataAbertura()
        );
    }
}
