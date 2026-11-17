
package mz.banco.wsd.controller;

import mz.banco.wsd.entity.Funcionario;
import mz.banco.wsd.service.FuncionarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    // Cadastrar funcionário
    @PostMapping
    public ResponseEntity<Funcionario> cadastrar(
            @RequestBody Funcionario funcionario) {

        System.out.println("Nome: " + funcionario.getNome());
        System.out.println("Email: " + funcionario.getEmail());

        System.out.println("Senha recebida: "
                + (funcionario.getSenha() == null ? "NULL" : "SIM"));

        Funcionario novoFuncionario =
                funcionarioService.cadastrar(funcionario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novoFuncionario);
    }

    // Listar funcionários
    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {

        return ResponseEntity.ok(
                funcionarioService.listarTodos()
        );
    }

    // Buscar funcionário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                funcionarioService.buscarPorId(id)
        );
    }

    // Desactivar funcionário
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Funcionario> desactivar(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                funcionarioService.desactivar(id)
        );
    }

    // Activar funcionário
    @PutMapping("/{id}/activar")
    public ResponseEntity<Funcionario> activar(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                funcionarioService.activar(id)
        );
    }
}