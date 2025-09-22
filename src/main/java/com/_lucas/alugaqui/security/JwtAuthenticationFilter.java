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

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        //Evitando numeros e strings magicas
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

        if (email != null && SecurityContextHolder.getContext().getAuthentication() != null){
            UserDetails userDetails = this.userDetailsService.loadByEmail(email);

            if(jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }

        chain.doFilter(request, response);

    }
}
