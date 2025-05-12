package com.backend.bankingapplication.security.filter;

import com.backend.bankingapplication.core.cache.service.RedisService;
import com.backend.bankingapplication.core.constants.KeyPrefix;
import com.backend.bankingapplication.security.resolver.TokenResolver;
import com.backend.bankingapplication.security.service.AuthorizedUserDetailsService;
import com.backend.bankingapplication.security.service.impl.ClaimService;
import com.backend.bankingapplication.core.exception.TokenRevokedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final ClaimService claimService;
    private final RedisService redisService;
    private final TokenResolver tokenResolver;
    private final OrRequestMatcher permittedUrlsRequestMatcher;
    private final AuthorizedUserDetailsService userDetailsService;

    public SecurityFilter(
            @Qualifier("compositeTokenResolver") TokenResolver tokenResolver, ClaimService claimService,
            AuthorizedUserDetailsService userDetailsService, SecurityFilterConfiguration filterConfiguration, RedisService redisService
    ) {
        this.redisService = redisService;
        this.claimService = claimService;
        this.tokenResolver = tokenResolver;
        this.userDetailsService = userDetailsService;

        List<AntPathRequestMatcher> requestMatchers = new ArrayList<>();

        filterConfiguration.getPermittedUrls()
                .forEach((permittedUri) -> requestMatchers.add(new AntPathRequestMatcher(permittedUri)));

        this.permittedUrlsRequestMatcher = new OrRequestMatcher(requestMatchers.toArray(new AntPathRequestMatcher[0]));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (this.permittedUrlsRequestMatcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            SecurityContext securityContext = SecurityContextHolder.getContext();

            Authentication authentication = createAuthentication(request);
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException exception) {
            log.warn(
                    "Authentication exception with error message {}, remote ip address {}", exception.getMessage(), request.getRemoteAddr()
            );
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        }
    }

    private Authentication createAuthentication(HttpServletRequest request) {
        String accessToken = tokenResolver.resolve(request);

        if (redisService.exists(KeyPrefix.ACCESS_TOKEN_PREFIX + ":" + accessToken)) {
            log.warn("Access token [{}] from IP [{}] is in blacklist", accessToken, request.getRemoteAddr());
            throw new TokenRevokedException("This token has been revoked");
        }

        UserDetails userDetails;
        try {
            Long userId = claimService.getUserId(accessToken);
            userDetails = userDetailsService.loadByUserId(userId);
        } catch (ExpiredJwtException exception) {
            log.warn("JWT token expired, message {}", exception.getMessage());
            throw new InsufficientAuthenticationException("Token expired");
        } catch (SignatureException exception) {
            log.warn("Invalid JWT signature, message {}", exception.getMessage());
            throw new BadCredentialsException("Invalid token signature");
        } catch (UsernameNotFoundException exception) {
            log.warn("Username not found, message {}", exception.getMessage());
            throw new AuthenticationServiceException("Username not found");
        } catch (JwtException exception) {
            log.warn("Invalid JWT token, message {}", exception.getMessage());
            throw new AuthenticationServiceException("Invalid token", exception);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
