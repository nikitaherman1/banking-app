package com.backend.bankingapplication.app.mapper;

import com.backend.bankingapplication.app.dto.create.CreatePhoneDataDTO;
import com.backend.bankingapplication.app.dto.response.PhoneDataResponseDTO;
import com.backend.bankingapplication.app.entity.PhoneData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhoneDataMapper {

    PhoneDataMapper INSTANCE = Mappers.getMapper(PhoneDataMapper.class);

    PhoneData toEntity(String phone);

    PhoneDataResponseDTO toDto(PhoneData phoneData);

    PhoneData toEntity(CreatePhoneDataDTO createPhoneDataDTO);
}
