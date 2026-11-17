package mz.banco.wsd.controller;

import mz.banco.wsd.dto.TransferenciaRequest;
import mz.banco.wsd.dto.TransferenciaResponse;
import mz.banco.wsd.dto.HistoricoTransacaoResponse;
import mz.banco.wsd.entity.Transacao;
import mz.banco.wsd.service.TransacaoService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    // TRANSFERIR DINHEIRO
    @PostMapping("/transferir")
    public ResponseEntity<TransferenciaResponse> transferir(
            @RequestBody TransferenciaRequest request,
            Authentication authentication) {

        String emailCliente = authentication.getName();

        Transacao transacao = transacaoService.transferir(
                emailCliente,
                request.getNumeroContaOrigem(),
                request.getNumeroContaDestino(),
                request.getPin(),
                request.getValor()
        );

        TransferenciaResponse resposta = new TransferenciaResponse(
                transacao.getId(),
                transacao.getContaOrigem().getNumeroConta(),
                transacao.getContaDestino().getNumeroConta(),
                transacao.getValor(),
                transacao.getDataTransacao(),
                transacao.getEstado()
        );

        return ResponseEntity.ok(resposta);
    }

    // CONSULTAR HISTÓRICO
    @GetMapping("/historico/{numeroConta}")
    public ResponseEntity<List<HistoricoTransacaoResponse>> consultarHistorico(
            @PathVariable String numeroConta,
            Authentication authentication) {

        String emailCliente = authentication.getName();

        List<Transacao> transacoes =
                transacaoService.consultarHistorico(emailCliente, numeroConta);

        List<HistoricoTransacaoResponse> historico = transacoes.stream()
                .map(transacao -> new HistoricoTransacaoResponse(
                        transacao.getId(),
                        transacao.getTipo(),
                        transacao.getValor(),
                        transacao.getDataTransacao(),
                        transacao.getContaOrigem() != null
                                ? transacao.getContaOrigem().getNumeroConta()
                                : null,
                        transacao.getContaDestino().getNumeroConta(),
                        transacao.getEstado()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(historico);
    }
}