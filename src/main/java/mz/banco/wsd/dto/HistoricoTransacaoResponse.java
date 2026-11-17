package mz.banco.wsd.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistoricoTransacaoResponse {

    private Long id;
    private String tipo;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;
    private String contaOrigem;
    private String contaDestino;
    private String estado;

    public HistoricoTransacaoResponse(
            Long id,
            String tipo,
            BigDecimal valor,
            LocalDateTime dataTransacao,
            String contaOrigem,
            String contaDestino,
            String estado) {

        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.dataTransacao = dataTransacao;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public String getContaOrigem() {
        return contaOrigem;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public String getEstado() {
        return estado;
    }
}