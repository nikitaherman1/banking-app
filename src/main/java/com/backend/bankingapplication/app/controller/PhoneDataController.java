package com.backend.bankingapplication.app.controller;

import com.backend.bankingapplication.app.dto.create.CreatePhoneDataDTO;
import com.backend.bankingapplication.app.dto.update.UpdatePhoneDataDTO;
import com.backend.bankingapplication.app.service.PhoneDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/phones")
public class PhoneDataController {

    private final PhoneDataService phoneDataService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@ownerShipService.checkOwner(#request)")
    public void save(@RequestBody @Valid CreatePhoneDataDTO createPhoneDataDTO, HttpServletRequest request) {
        phoneDataService.save(createPhoneDataDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ownerShipService.checkOwnerShip(T(com.backend.bankingapplication.app.entity.PhoneData), #id, #request)")
    public void update(
            @PathVariable Long id, @RequestBody @Valid UpdatePhoneDataDTO updatePhoneDataDTO, HttpServletRequest request
    ) {
        phoneDataService.update(updatePhoneDataDTO, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ownerShipService.checkOwnerShip(T(com.backend.bankingapplication.app.entity.PhoneData), #id, #request)")
    public void deleteById(@PathVariable @NotNull Long id, HttpServletRequest request) {
        phoneDataService.deleteById(id);
    }
}
