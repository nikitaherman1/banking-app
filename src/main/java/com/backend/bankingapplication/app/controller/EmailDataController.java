package com.backend.bankingapplication.app.controller;

import com.backend.bankingapplication.app.dto.create.CreateEmailDataDTO;
import com.backend.bankingapplication.app.dto.update.UpdateEmailDataDTO;
import com.backend.bankingapplication.app.service.EmailDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/emails")
public class EmailDataController {

    private final EmailDataService emailDataService;

    @PostMapping
    @PreAuthorize("@ownerShipService.checkOwner(#request)")
    public void save(@RequestBody @Valid CreateEmailDataDTO createEmailDataDTO, HttpServletRequest request) {
        emailDataService.save(createEmailDataDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ownerShipService.checkOwnerShip(T(com.backend.bankingapplication.app.entity.EmailData), #id, #request)")
    public void update(
            @PathVariable Long id, @RequestBody @Valid UpdateEmailDataDTO updateEmailDataDTO, HttpServletRequest request
    ) {
        emailDataService.update(updateEmailDataDTO, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ownerShipService.checkOwnerShip(T(com.backend.bankingapplication.app.entity.EmailData), #id, #request)")
    public void deleteById(@PathVariable @NotNull Long id, HttpServletRequest request) {
        emailDataService.deleteById(id);
    }
}
