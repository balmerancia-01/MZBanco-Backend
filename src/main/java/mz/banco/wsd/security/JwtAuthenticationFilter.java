
package mz.banco.wsd.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String cabecalho = request.getHeader("Authorization");

        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cabecalho.substring(7);

        try {

            String email = jwtService.extrairEmail(token);
            String tipo = jwtService.extrairTipo(token);
            String cargo = jwtService.extrairCargo(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtService.validarToken(token, email)) {

                    String autoridade;

                    if ("CLIENTE".equals(tipo)) {

                        autoridade = "ROLE_CLIENTE";

                    } else if ("FUNCIONARIO".equals(tipo)) {

                        if ("ADMINISTRADOR".equals(cargo)) {
                            autoridade = "ROLE_ADMINISTRADOR";

                        } else if ("FUNCIONARIO".equals(cargo)) {
                            autoridade = "ROLE_FUNCIONARIO";

                        } else {
                            throw new IllegalArgumentException("Cargo inválido");
                        }

                    } else {
                        throw new IllegalArgumentException("Tipo de utilizador inválido");
                    }

                    UsernamePasswordAuthenticationToken autenticacao =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(
                                            new SimpleGrantedAuthority(autoridade)
                                    )
                            );

                    autenticacao.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(autenticacao);
                }
            }

        } catch (Exception e) {

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
