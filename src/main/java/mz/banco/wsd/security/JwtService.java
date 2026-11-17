
package mz.banco.wsd.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey chaveSecreta;
    private final long tempoExpiracao;

    public JwtService(
            @Value("${jwt.secret}") String segredo,
            @Value("${jwt.expiration}") long expiracao) {

        this.chaveSecreta = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(segredo)
        );

        this.tempoExpiracao = expiracao;
    }

    // Gerar token para clientes (mantém compatibilidade)
    public String gerarToken(String email) {
        return gerarToken(email, "CLIENTE", "CLIENTE");
    }

    // Gerar token com tipo de utilizador e cargo
    public String gerarToken(
            String email,
            String tipo,
            String cargo) {

        Date agora = new Date();

        Date expiracao = new Date(
                agora.getTime() + tempoExpiracao
        );

        return Jwts.builder()
                .subject(email)
                .claim("tipo", tipo)
                .claim("cargo", cargo)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chaveSecreta)
                .compact();
    }

    // Extrair email
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    // Extrair tipo de utilizador
    public String extrairTipo(String token) {
        return extrairClaims(token).get("tipo", String.class);
    }

    // Extrair cargo
    public String extrairCargo(String token) {
        return extrairClaims(token).get("cargo", String.class);
    }

    // Validar token
    public boolean validarToken(String token, String email) {

        String emailToken = extrairEmail(token);

        return emailToken != null
                && emailToken.equals(email);
    }

    // Ler informações e validar assinatura e expiração
    private Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(chaveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
