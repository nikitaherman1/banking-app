package com.backend.bankingapplication.security.service.impl;

import com.backend.bankingapplication.core.cache.service.RedisService;
import com.backend.bankingapplication.core.constants.KeyPrefix;
import com.backend.bankingapplication.security.dto.AuthenticationPayloadDTO;
import com.backend.bankingapplication.security.dto.RegistrationPayloadDTO;
import com.backend.bankingapplication.security.model.AuthorizedUserDetails;
import com.backend.bankingapplication.security.properties.SecurityProperties;
import com.backend.bankingapplication.security.resolver.TokenResolver;
import com.backend.bankingapplication.security.util.CookieUtil;
import com.backend.bankingapplication.app.entity.BankingUser;
import com.backend.bankingapplication.app.service.BankingUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.backend.bankingapplication.security.util.CookieUtil.ACCESS_TOKEN_COOKIE;
import static com.backend.bankingapplication.security.util.CookieUtil.createCookie;

@Slf4j
@Service
public class AuthService {

    private final BankingUserService userService;
    private final TokenService tokenService;
    private final RedisService redisService;
    private final TokenResolver tokenResolver;
    private final SecurityProperties securityProperties;
    private final AuthenticationManager authenticationManager;

    public AuthService(@Qualifier("compositeTokenResolver") TokenResolver tokenResolver, RedisService redisService,
                       BankingUserService userService, TokenService tokenService, SecurityProperties securityProperties,
                       AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.redisService = redisService;
        this.tokenResolver = tokenResolver;
        this.securityProperties = securityProperties;
        this.authenticationManager = authenticationManager;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication || null == authentication.getPrincipal()) {
            throw new AccessDeniedException("Authentication required");
        }
        AuthorizedUserDetails userDetails =(AuthorizedUserDetails)authentication.getPrincipal();
        return userDetails.getUserId();
    }

    public void registration(RegistrationPayloadDTO payloadDTO, HttpServletResponse response) {
        BankingUser bankingUser = userService.save(payloadDTO);
        AuthorizedUserDetails userDetails = new AuthorizedUserDetails(bankingUser);

        createAndSetAuthCookie(userDetails, response);
    }

    public void authenticate(AuthenticationPayloadDTO payloadDTO, HttpServletResponse response) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(payloadDTO.getLogin(), payloadDTO.getPassword());
        Authentication authenticationResult = authenticationManager.authenticate(authenticationToken);

        AuthorizedUserDetails userDetails = (AuthorizedUserDetails) authenticationResult.getPrincipal();
        createAndSetAuthCookie(userDetails, response);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenResolver.resolve(request);

        redisService.setAndExpire(KeyPrefix.ACCESS_TOKEN_PREFIX + ":" + token, token, securityProperties.getExpiration());
        CookieUtil.clearCookies(request, response);
    }

    private void createAndSetAuthCookie(AuthorizedUserDetails userDetails, HttpServletResponse response) {
        String token = tokenService.generateToken(userDetails);
        Cookie cookie = createCookie(ACCESS_TOKEN_COOKIE, token, securityProperties.getExpiration());

        response.addCookie(cookie);
    }
}
