package com.backend.bankingapplication.app.mapper;

import com.backend.bankingapplication.app.dto.response.BankingUserProfileDTO;
import com.backend.bankingapplication.app.dto.response.BankingUserResponseDTO;
import com.backend.bankingapplication.security.dto.RegistrationPayloadDTO;
import com.backend.bankingapplication.app.entity.BankingUser;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {EmailDataMapper.class, PhoneDataMapper.class})
public interface BankingUserMapper {

    BankingUserMapper INSTANCE = Mappers.getMapper(BankingUserMapper.class);

    @Mapping(target = "account.balance", source = "balance")
    @Mapping(target = "account.initialBalance", source = "balance")
    BankingUser toEntity(RegistrationPayloadDTO dto);

    BankingUserResponseDTO toDto(BankingUser user);

    @Mapping(target = "balance", source = "account.balance")
    BankingUserProfileDTO toProfileDTO(BankingUser user);

    @AfterMapping
    default void linkRelations(@MappingTarget BankingUser user, RegistrationPayloadDTO dto) {
        user.getAccount().setBankingUser(user);
        user.getEmails().forEach(email -> email.setBankingUser(user));
        user.getPhones().forEach(phone -> phone.setBankingUser(user));
    }
}