package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.create.CreateEmailDataDTO;
import com.backend.bankingapplication.app.dto.update.UpdateEmailDataDTO;
import com.backend.bankingapplication.app.entity.BankingUser;
import com.backend.bankingapplication.app.entity.EmailData;
import com.backend.bankingapplication.app.mapper.EmailDataMapper;
import com.backend.bankingapplication.app.repository.EmailDataRepository;
import com.backend.bankingapplication.core.cache.event.UpdateUserEvent;
import com.backend.bankingapplication.core.exception.BadRequestDataException;
import com.backend.bankingapplication.security.service.impl.AuthService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDataService {

    private static final Long MIN_EMAILS_COUNT = 1L;

    private final EmailDataMapper emailDataMapper = EmailDataMapper.INSTANCE;

    private final AuthService authService;
    private final BankingUserService userService;
    private final EmailDataRepository emailDataRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void save(CreateEmailDataDTO createEmailDataDTO) {
        Long currentUserId = authService.getCurrentUserId();
        log.info(
                "User with id {} is trying to create a new email address {}",
                currentUserId, createEmailDataDTO.getEmail()
        );
        validateEmailOnExists(createEmailDataDTO.getEmail());

        BankingUser bankingUser = userService.findById(currentUserId);
        EmailData emailData = emailDataMapper.toEntity(createEmailDataDTO);
        emailData.setBankingUser(bankingUser);

        emailDataRepository.save(emailData);
        eventPublisher.publishEvent(new UpdateUserEvent(currentUserId));
    }

    public void update(UpdateEmailDataDTO updateEmailDataDTO, Long id) {
        Long currentUserId = authService.getCurrentUserId();
        log.info(
                "User with id {} is trying to update an old email address on {}",
                currentUserId, updateEmailDataDTO.getEmail()
        );
        validateEmailOnExists(updateEmailDataDTO.getEmail());

        EmailData emailData = findById(id);
        emailData.setEmail(updateEmailDataDTO.getEmail());

        emailDataRepository.save(emailData);
        eventPublisher.publishEvent(new UpdateUserEvent(currentUserId));
    }

    public void deleteById(Long id) {
        Long currentUserId = authService.getCurrentUserId();
        log.info("User with id {}  is trying to delete email address {}", currentUserId, id);

        if (Objects.equals(emailDataRepository.countEmailsByUserId(currentUserId), MIN_EMAILS_COUNT)) {
            throw new BadRequestDataException("You can't delete a single mailbox");
        }
        emailDataRepository.deleteById(id);
        eventPublisher.publishEvent(new UpdateUserEvent(currentUserId));
    }

    public EmailData findById(Long id) {
        return emailDataRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    private void validateEmailOnExists(String email) {
        if (existsByEmail(email)) {
            throw new EntityExistsException("Email data already exists");
        }
    }

    public boolean existsByEmail(String email) {
        return emailDataRepository.existsByEmail(email);
    }
}
