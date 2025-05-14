package com.backend.bankingapplication.service;

import com.backend.bankingapplication.app.dto.create.TransferRequestDTO;
import com.backend.bankingapplication.app.service.TransferService;
import com.backend.bankingapplication.app.service.TransferTransactionService;
import com.backend.bankingapplication.security.service.impl.AuthService;
import com.sun.jdi.request.DuplicateRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    private TransferRequestDTO transferRequestDTO;

    private final Long fromUserId = 1L;
    private final BigDecimal value = new BigDecimal("100.00");

    @InjectMocks
    private TransferService transferService;

    @Mock
    private AuthService authService;

    @Mock
    private TransferTransactionService transferTransactionService;

    @BeforeEach
    void setUp() {
        transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(2L);
        transferRequestDTO.setValue(value);
    }

    @Test
    void shouldSuccessTransfer() {
        when(authService.getCurrentUserId()).thenReturn(fromUserId);

        transferService.trySecureTransfer(transferRequestDTO);

        verify(transferTransactionService).secureTransfer(transferRequestDTO, fromUserId);
    }

    @Test
    void shouldThrowDuplicationRequestException_whenIdempotencyKeyIsAlreadyUsed() {
        String errorMessage = "Duplicate transfer request";

        when(authService.getCurrentUserId()).thenReturn(fromUserId);
        doThrow(new DuplicateRequestException(errorMessage))
                .when(transferTransactionService)
                .secureTransfer(transferRequestDTO, fromUserId);

        DuplicateRequestException exception = assertThrows(
                DuplicateRequestException.class,
                () -> transferService.trySecureTransfer(transferRequestDTO)
        );
        assertEquals(errorMessage, exception.getMessage());

        verify(transferTransactionService).secureTransfer(transferRequestDTO, fromUserId);
    }

    @Test
    void shouldThrowException_whenSecureTransfer() {
        String errorMessage = "Some exception";

        when(authService.getCurrentUserId()).thenReturn(fromUserId);
        doThrow(new RuntimeException(errorMessage)).when(transferTransactionService)
                .secureTransfer(transferRequestDTO, fromUserId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> transferService.trySecureTransfer(transferRequestDTO)
        );

        assertEquals(errorMessage, exception.getMessage());
        verify(transferTransactionService).secureTransfer(transferRequestDTO, fromUserId);
    }
}
