
package mz.banco.wsd.service;

import mz.banco.wsd.entity.Funcionario;
import mz.banco.wsd.repository.FuncionarioRepository;
import mz.banco.wsd.dto.LoginRequest;
import mz.banco.wsd.dto.LoginResponse;
import mz.banco.wsd.security.JwtService;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            JwtService jwtService) {

        this.funcionarioRepository = funcionarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Registar funcionário
    public Funcionario cadastrar(Funcionario funcionario) {

        if (funcionario.getNome() == null || funcionario.getNome().isBlank()) {
            throw new RuntimeException("O nome é obrigatório");
        }

        if (funcionario.getEmail() == null || funcionario.getEmail().isBlank()) {
            throw new RuntimeException("O email é obrigatório");
        }

        if (funcionario.getSenha() == null || funcionario.getSenha().isBlank()) {
            throw new RuntimeException("A senha é obrigatória");
        }

        if (funcionario.getSenha().length() < 8) {
            throw new RuntimeException("A senha deve ter pelo menos 8 caracteres");
        }

        if (funcionarioRepository.existsByEmail(funcionario.getEmail())) {
            throw new RuntimeException("Este email já está registado");
        }

        funcionario.setIdFuncionario(null);

        funcionario.setSenha(
            passwordEncoder.encode(funcionario.getSenha())
        );

        funcionario.setEstado("activo");

        return funcionarioRepository.save(funcionario);
    }

    // Login de funcionário
    public LoginResponse autenticar(LoginRequest pedido) {

        if (pedido.getEmail() == null || pedido.getEmail().isBlank()
                || pedido.getSenha() == null || pedido.getSenha().isBlank()) {

            throw new RuntimeException("Email e senha são obrigatórios");
        }

        Funcionario funcionario = funcionarioRepository
                .findByEmail(pedido.getEmail())
                .orElseThrow(() ->
                    new RuntimeException("Funcionário não encontrado")
                );

        if (!passwordEncoder.matches(
                pedido.getSenha(),
                funcionario.getSenha())) {

            throw new RuntimeException("Senha incorrecta");
        }

        if (!"ACTIVO".equalsIgnoreCase(funcionario.getEstado())) {
            throw new RuntimeException("Funcionário inactivo");
        }

        if (!"ADMINISTRADOR".equalsIgnoreCase(funcionario.getCargo())
                && !"FUNCIONARIO".equalsIgnoreCase(funcionario.getCargo())) {

            throw new RuntimeException("Cargo inválido");
        }

        String token = jwtService.gerarToken(
                funcionario.getEmail(),
                "FUNCIONARIO",
                funcionario.getCargo().toUpperCase()
        );

        return new LoginResponse(
                funcionario.getIdFuncionario(),
                funcionario.getNome(),
                funcionario.getEmail(),
                "Login efectuado com sucesso",
                token,
                "FUNCIONARIO",
                funcionario.getCargo().toUpperCase()
        );
    }

    // Listar funcionários
    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    // Procurar funcionário por ID
    public Funcionario buscarPorId(Integer id) {

        return funcionarioRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Funcionário não encontrado")
            );
    }

    // Desactivar funcionário
    public Funcionario desactivar(Integer id) {

        Funcionario funcionario = buscarPorId(id);
        funcionario.setEstado("INACTIVO");

        return funcionarioRepository.save(funcionario);
    }

    // Activar funcionário
    public Funcionario activar(Integer id) {

        Funcionario funcionario = buscarPorId(id);
        funcionario.setEstado("ACTIVO");

        return funcionarioRepository.save(funcionario);
    }
}

