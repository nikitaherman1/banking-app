package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.create.CreatePhoneDataDTO;
import com.backend.bankingapplication.app.dto.update.UpdatePhoneDataDTO;
import com.backend.bankingapplication.app.entity.BankingUser;
import com.backend.bankingapplication.app.entity.PhoneData;
import com.backend.bankingapplication.app.mapper.PhoneDataMapper;
import com.backend.bankingapplication.app.repository.PhoneDataRepository;
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
public class PhoneDataService {

    private static final Long MIN_PHONE_COUNT = 1L;

    private final PhoneDataMapper phoneDataMapper = PhoneDataMapper.INSTANCE;

    private final AuthService authService;
    private final BankingUserService userService;
    private final PhoneDataRepository phoneDataRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void save(CreatePhoneDataDTO createPhoneDataDTO) {
        Long currentUserId = authService.getCurrentUserId();
        log.info(
                "User with id {} is trying to create a new phone {}",
                currentUserId, createPhoneDataDTO.getPhone()
        );
        validatePhoneOnExists(createPhoneDataDTO.getPhone());

        BankingUser bankingUser = userService.findById(currentUserId);
        PhoneData phoneData = phoneDataMapper.toEntity(createPhoneDataDTO);
        phoneData.setBankingUser(bankingUser);

        phoneDataRepository.save(phoneData);
        eventPublisher.publishEvent(new UpdateUserEvent(currentUserId));
    }

    public void update(UpdatePhoneDataDTO updatePhoneDataDTO, Long id) {
        Long currentUserId = authService.getCurrentUserId();
        log.info(
                "User with id {} is trying to update an old phone on {}",
                currentUserId, updatePhoneDataDTO.getPhone()
        );
        validatePhoneOnExists(updatePhoneDataDTO.getPhone());

        PhoneData phoneData = findById(id);
        phoneData.setPhone(updatePhoneDataDTO.getPhone());

        phoneDataRepository.save(phoneData);
        eventPublisher.publishEvent(new UpdateUserEvent(currentUserId));
    }

    public void deleteById(Long id) {
        Long currentUserId = authService.getCurrentUserId();
        log.info("User with id {}  is trying to delete phone {}", currentUserId, id);

        if (Objects.equals(phoneDataRepository.countPhonesByUserId(currentUserId), MIN_PHONE_COUNT)) {
            throw new BadRequestDataException("You can't delete a single number");
        }
        phoneDataRepository.deleteById(id);
        eventPublisher.publishEvent(new UpdateUserEvent(currentUserId));
    }

    public PhoneData findById(Long id) {
        return phoneDataRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    private void validatePhoneOnExists(String phone) {
        if (existsByPhone(phone)) {
            throw new EntityExistsException("Phone data already exists");
        }
    }

    public boolean existsByPhone(String phone) {
        return phoneDataRepository.existsByPhone(phone);
    }
}
