package com.backend.bankingapplication.security.resolver;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenResolver {

    String resolve(HttpServletRequest request);
}
