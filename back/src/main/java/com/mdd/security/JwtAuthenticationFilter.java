package com.mdd.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre exécuté une fois par requête, qui authentifie l'utilisateur à partir du token JWT
 * présent dans l'en-tête {@code Authorization: Bearer <token>}.
 * <p>
 * Si le token est valide, l'email qu'il contient est placé dans le contexte de sécurité
 * (avec le rôle {@code ROLE_USER}), ce qui le rend accessible via {@code @AuthenticationPrincipal}.
 * Si le token est absent ou invalide, la requête continue sans authentification
 * et sera rejetée (401) par la configuration de sécurité sur les routes protégées.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Extrait et valide le token JWT de la requête, puis authentifie l'utilisateur
     * dans le contexte de sécurité si le token est valide.
     *
     * @param request     la requête HTTP entrante
     * @param response    la réponse HTTP
     * @param filterChain la chaîne de filtres à poursuivre
     * @throws ServletException en cas d'erreur de traitement du filtre
     * @throws IOException      en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtService.isTokenValid(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                String email = jwtService.extractEmail(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}