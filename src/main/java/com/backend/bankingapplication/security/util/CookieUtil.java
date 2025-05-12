package com.backend.bankingapplication.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";

    public static String getCookieValueByName(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public static Cookie createCookie(String cookieName, String cookieValue, Integer expiration) {
        Cookie cookie = new Cookie(cookieName, cookieValue);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiration);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    public static void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setValue(null);
                cookie.setHttpOnly(true);

                response.addCookie(cookie);
            }
        }
    }
}
