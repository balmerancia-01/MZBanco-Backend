
package mz.banco.wsd.controller;

import mz.banco.wsd.dto.LoginRequest;
import mz.banco.wsd.dto.LoginResponse;
import mz.banco.wsd.service.ClienteService;
import mz.banco.wsd.service.FuncionarioService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;

    public AuthController(
            ClienteService clienteService,
            FuncionarioService funcionarioService) {

        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest pedido) {

        // Primeiro procurar o utilizador entre os clientes
        if (clienteService.existeEmail(pedido.getEmail())) {
            return clienteService.autenticar(pedido);
        }

        // Se não for cliente, procurar entre os funcionários
        return funcionarioService.autenticar(pedido);
    }
}
