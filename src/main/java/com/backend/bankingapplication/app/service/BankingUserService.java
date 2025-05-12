package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.filter.BankingUserFilterDTO;
import com.backend.bankingapplication.app.dto.response.BankingUserProfileDTO;
import com.backend.bankingapplication.app.dto.response.BankingUserResponseDTO;
import com.backend.bankingapplication.app.entity.BankingUser;
import com.backend.bankingapplication.app.mapper.BankingUserMapper;
import com.backend.bankingapplication.app.repository.BankingUserRepository;
import com.backend.bankingapplication.core.builder.SpecificationBuilder;
import com.backend.bankingapplication.core.constants.KeyPrefix;
import com.backend.bankingapplication.security.dto.RegistrationPayloadDTO;
import io.netty.util.internal.StringUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.backend.bankingapplication.app.entity.BankingUser.*;
import static com.backend.bankingapplication.app.entity.EmailData.EMAIL;
import static com.backend.bankingapplication.app.entity.PhoneData.PHONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankingUserService {

    private final BankingUserMapper userMapper = BankingUserMapper.INSTANCE;

    private final BankingUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BankingUser findById(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Cacheable(cacheNames = KeyPrefix.USER_EXISTS_PREFIX, key = "#userId")
    public boolean existsByUserId(long userId) {
        return userRepository.existsById(userId);
    }

    @Cacheable(cacheNames = KeyPrefix.USER_PROFILES_PREFIX, key = "#userId")
    public BankingUserProfileDTO getCurrentUserProfile(Long userId) {
        BankingUser bankingUser = findById(userId);

        return userMapper.toProfileDTO(bankingUser);
    }

    @CacheEvict(cacheNames = KeyPrefix.USER_PROFILES_PREFIX, key = "#userId")
    public void evictUserProfileCache(Long userId) {
        // invalidate user profile cache
    }

    @CacheEvict(cacheNames = KeyPrefix.USER_PROFILES_PREFIX, allEntries = true)
    public void evictUserProfilesCache() {
        // invalidate all user profiles cache
        log.info("Invalidate all user profiles cache");
    }

    public Page<BankingUserResponseDTO> getAllByFilterPaged(long currentUserId, BankingUserFilterDTO filter, Pageable pageable) {
        SpecificationBuilder builder = new SpecificationBuilder();

        Specification<BankingUser> specification = builder
                .andGreaterThan(filter.getDateOfBirth(), DATE_OF_BIRTH)
                .andLike(filter.getName(), NAME)
                .withJoin(EMAILS, (join, cb) -> StringUtil.isNullOrEmpty(filter.getEmail())
                        ? null
                        : cb.equal(join.get(EMAIL), filter.getEmail()))
                .withJoin(PHONES, (join, cb) -> StringUtil.isNullOrEmpty(filter.getPhone())
                        ? null
                        : cb.equal(join.get(PHONE), filter.getPhone()))
                .andNotEquals(currentUserId, ID)
                .build();

        return userRepository.findAll(specification, pageable)
                .map(userMapper::toDto);
    }

    public BankingUser save(RegistrationPayloadDTO payloadDTO) {
        BankingUser bankingUser = userMapper.toEntity(payloadDTO);
        bankingUser.setPassword(passwordEncoder.encode(bankingUser.getPassword()));

        return userRepository.save(bankingUser);
    }
}
