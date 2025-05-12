package com.backend.bankingapplication.app.mapper;

import com.backend.bankingapplication.app.dto.create.CreateEmailDataDTO;
import com.backend.bankingapplication.app.dto.response.EmailDataResponseDTO;
import com.backend.bankingapplication.app.entity.EmailData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailDataMapper {

    EmailDataMapper INSTANCE = Mappers.getMapper(EmailDataMapper.class);

    EmailData toEntity(String email);

    EmailDataResponseDTO toDto(EmailData emailData);

    EmailData toEntity(CreateEmailDataDTO createEmailDataDTO);
}
