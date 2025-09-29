package com._lucas.alugaqui.security;

import com._lucas.alugaqui.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService userDetailsService;

    public JwtAuthenticationFilter(
            UsuarioRepository usuarioRepository,
            JwtUtil jwtUtil,
            CustomUserDetailService userDetailsService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔓 Endpoints públicos (sem necessidade de JWT)
        List<String> publicPrefixes = List.of(
                "/api/login",
                "/api/change-password",
                "/swagger-ui",
                "/swagger-ui/",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs"
        );

        // Se a requisição começar com algum prefixo público → pula autenticação
        if (publicPrefixes.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        final String AUTHORIZATION_HEADER = "Authorization";
        final String BEARER_PREFIX = "Bearer ";
        final int BEARER_SIZE = BEARER_PREFIX.length();

        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            jwt = authorizationHeader.substring(BEARER_SIZE);
            email = jwtUtil.extractEmail(jwt);
        }

        // Só autentica se ainda não houver autenticação no contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
