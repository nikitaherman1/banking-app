package com.backend.bankingapplication.security.service.impl;

import com.backend.bankingapplication.app.entity.EmailData;
import com.backend.bankingapplication.core.orm.entity.Ownable;
import com.backend.bankingapplication.app.entity.PhoneData;
import com.backend.bankingapplication.app.repository.EmailDataRepository;
import com.backend.bankingapplication.core.orm.repository.OwnerShipRepository;
import com.backend.bankingapplication.app.repository.PhoneDataRepository;
import com.backend.bankingapplication.app.service.BankingUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerShipService {

    private final BankingUserService userService;
    private final AuthService authService;
    private final PhoneDataRepository phoneDataRepository;
    private final EmailDataRepository emailDataRepository;

    public <T extends Ownable<I>, I extends Serializable> boolean checkOwnerShip(Class<T> tClass, Long resourceId, HttpServletRequest request) {
        Long currentUserId = authService.getCurrentUserId();
        checkOwner(request);

        OwnerShipRepository<T, I> repository = getRepository(tClass);
        boolean exists = repository.findByIdAndOwnerId(resourceId, currentUserId).isPresent();
        if (!exists) {
            logAndThrowAccessDeniedException(request);
        }
        return true;
    }

    public boolean checkOwner(HttpServletRequest request) {
        Long currentUserId = authService.getCurrentUserId();

        if (!userService.existsByUserId(currentUserId)) {
            logAndThrowAccessDeniedException(request);
        }
        return true;
    }

    /** * We safely cast here because we checked the class before with `equals`.
     * This ensures that the repository matches the expected generic type. */
    @SuppressWarnings("unchecked")
    private <I extends Serializable, T extends Ownable<I>> OwnerShipRepository<T, I> getRepository(Class<T> tClass) {
        if (tClass.equals(EmailData.class)) {
            return (OwnerShipRepository<T, I>) emailDataRepository;
        } else if (tClass.equals(PhoneData.class)) {
            return (OwnerShipRepository<T, I>) phoneDataRepository;
        }
        throw new IllegalArgumentException("Unsupported type class: " + tClass);
    }

    private void logAndThrowAccessDeniedException(HttpServletRequest request) {
        log.warn("Unauthorized access to resource from ip address {}", request.getRemoteAddr());
        throw new AccessDeniedException("Unauthorized access to resource");
    }
}
