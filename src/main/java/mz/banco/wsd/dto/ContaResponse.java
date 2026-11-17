package mz.banco.wsd.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaResponse {

    private Integer id;
    private String numeroConta;
    private String nomeCliente;
    private BigDecimal saldo;
    private String estado;
    private LocalDateTime dataAbertura;

    public ContaResponse(Integer id, String numeroConta,
                         String nomeCliente, BigDecimal saldo,
                         String estado, LocalDateTime dataAbertura) {

        this.id = id;
        this.numeroConta = numeroConta;
        this.nomeCliente = nomeCliente;
        this.saldo = saldo;
        this.estado = estado;
        this.dataAbertura = dataAbertura;
    }

    public Integer getId() {
        return id;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }
}