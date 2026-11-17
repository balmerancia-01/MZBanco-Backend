
package mz.banco.wsd.controller;

import mz.banco.wsd.entity.Cliente;
import mz.banco.wsd.service.ClienteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // REGISTAR CLIENTE
    @PostMapping
    public ResponseEntity<Cliente> cadastrar(
            @RequestBody Cliente cliente) {

        Cliente clienteGuardado = clienteService.cadastrar(cliente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteGuardado);
    }
}
