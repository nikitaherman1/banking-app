package com.backend.bankingapplication.security.resolver.impl;

import com.backend.bankingapplication.security.resolver.TokenResolver;
import com.backend.bankingapplication.security.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import static com.backend.bankingapplication.security.util.CookieUtil.ACCESS_TOKEN_COOKIE;

@Component
public class CookieTokenResolver implements TokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return "";
        }
        return CookieUtil.getCookieValueByName(cookies, ACCESS_TOKEN_COOKIE);
    }
}
