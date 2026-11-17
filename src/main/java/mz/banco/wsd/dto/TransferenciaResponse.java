package mz.banco.wsd.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferenciaResponse {

    private Long id;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;
    private String estado;

    public TransferenciaResponse(
            Long id,
            String contaOrigem,
            String contaDestino,
            BigDecimal valor,
            LocalDateTime dataTransacao,
            String estado) {

        this.id = id;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.dataTransacao = dataTransacao;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getContaOrigem() {
        return contaOrigem;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public String getEstado() {
        return estado;
    }
}
