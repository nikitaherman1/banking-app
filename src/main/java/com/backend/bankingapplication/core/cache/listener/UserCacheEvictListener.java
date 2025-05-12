package com.backend.bankingapplication.core.cache.listener;

import com.backend.bankingapplication.app.service.BankingUserService;
import com.backend.bankingapplication.core.cache.event.UpdateUserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCacheEvictListener {

    private final BankingUserService userService;

    @EventListener
    public void handleUpdateUserEvent(UpdateUserEvent event) {
        userService.evictUserProfileCache(event.userId());
    }
}
