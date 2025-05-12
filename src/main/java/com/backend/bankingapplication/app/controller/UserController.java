package com.backend.bankingapplication.app.controller;

import com.backend.bankingapplication.app.dto.filter.BankingUserFilterDTO;
import com.backend.bankingapplication.app.dto.response.BankingUserProfileDTO;
import com.backend.bankingapplication.app.dto.response.BankingUserResponseDTO;
import com.backend.bankingapplication.app.service.BankingUserService;
import com.backend.bankingapplication.security.service.impl.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final BankingUserService userService;
    private final AuthService authService;

    @GetMapping("/current")
    @PreAuthorize("@ownerShipService.checkOwner(#request)")
    public BankingUserProfileDTO getCurrentUserProfile(HttpServletRequest request) {
        return userService.getCurrentUserProfile(authService.getCurrentUserId());
    }

    @GetMapping
    public Page<BankingUserResponseDTO> getAllByFilterPaged(BankingUserFilterDTO filterDTO, Pageable pageable) {
        return userService.getAllByFilterPaged(authService.getCurrentUserId(), filterDTO, pageable);
    }
}
