
package mz.banco.wsd.service;

import mz.banco.wsd.entity.Cliente;
import mz.banco.wsd.repository.ClienteRepository;
import mz.banco.wsd.dto.LoginRequest;
import mz.banco.wsd.dto.LoginResponse;
import mz.banco.wsd.security.JwtService;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ClienteService(
            ClienteRepository clienteRepository,
            JwtService jwtService) {

        this.clienteRepository = clienteRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // VERIFICAR SE O EMAIL EXISTE
    public boolean existeEmail(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        return clienteRepository.findByEmail(email).isPresent();
    }

    // REGISTAR CLIENTE
    public Cliente cadastrar(Cliente cliente) {

        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new RuntimeException("O nome é obrigatório.");
        }

        if (cliente.getNumeroBi() == null || cliente.getNumeroBi().isBlank()) {
            throw new RuntimeException("O número do BI é obrigatório.");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().isBlank()) {
            throw new RuntimeException("O telefone é obrigatório.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new RuntimeException("O email é obrigatório.");
        }

        if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
            throw new RuntimeException("A palavra-passe é obrigatória.");
        }

        if (cliente.getSenha().length() < 8) {
            throw new RuntimeException(
                    "A palavra-passe deve ter pelo menos 8 caracteres.");
        }

        if (existeEmail(cliente.getEmail())) {
            throw new RuntimeException("Este email já está registado.");
        }

        if (clienteRepository.findByNumeroBi(cliente.getNumeroBi()).isPresent()) {
            throw new RuntimeException("Este BI já está registado.");
        }

        // Não permitir que o utilizador escolha o próprio ID.
        cliente.setId(null);

        // Encriptar a palavra-passe.
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        // Definir estado inicial.
        cliente.setEstado("activo");

        return clienteRepository.save(cliente);
    }

    // AUTENTICAR CLIENTE
    public LoginResponse autenticar(LoginRequest pedido) {

        if (pedido == null
                || pedido.getEmail() == null
                || pedido.getEmail().isBlank()
                || pedido.getSenha() == null
                || pedido.getSenha().isBlank()) {

            throw new RuntimeException(
                    "Email e palavra-passe são obrigatórios.");
        }

        Cliente cliente = clienteRepository
                .findByEmail(pedido.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "Email ou palavra-passe incorrectos."));

        if (cliente.getSenha() == null
                || !passwordEncoder.matches(
                        pedido.getSenha(),
                        cliente.getSenha())) {

            throw new RuntimeException(
                    "Email ou palavra-passe incorrectos.");
        }

        if (!"activo".equalsIgnoreCase(cliente.getEstado())) {
            throw new RuntimeException("Cliente não está activo.");
        }

        // Gerar token JWT para o cliente.
        String token = jwtService.gerarToken(cliente.getEmail());

        return new LoginResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                "Login realizado com sucesso!",
                token
        );
    }
}

