package application.adapters.security;

import application.adapters.security.dtos.AuthenticatedUserPrincipal;
import application.domain.models.User;
import application.domain.services.user.LoadAuthenticatedUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final LoadAuthenticatedUserService loadAuthenticatedUserService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider,
                                   LoadAuthenticatedUserService loadAuthenticatedUserService) {
        this.jwtProvider = jwtProvider;
        this.loadAuthenticatedUserService = loadAuthenticatedUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);

        if (token != null && jwtProvider.validateToken(token)) {
            try {
                // El usuario se recarga de la BD: si fue bloqueado, el bloqueo aplica de inmediato
                User user = loadAuthenticatedUserService.load(token);
                AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}