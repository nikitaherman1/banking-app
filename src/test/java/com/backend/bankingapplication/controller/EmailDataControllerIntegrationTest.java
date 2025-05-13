package com.backend.bankingapplication.controller;

import com.backend.bankingapplication.app.dto.create.CreateEmailDataDTO;
import com.backend.bankingapplication.app.service.EmailDataService;
import com.backend.bankingapplication.security.service.impl.OwnerShipService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailDataControllerIntegrationTest {

    private static final String TEST_PATH = "/emails";
    private static final String TEST_EMAIL = "test@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;;

    private final OwnerShipService ownerShipService = mock(OwnerShipService.class);

    private final EmailDataService emailDataService = mock(EmailDataService.class);

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void configureRegistry(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Test
    void testSaveEmailData_shouldCallService_whenAuthorizedAndOwnerCheckPasses() throws Exception {
        when(ownerShipService.checkOwner(any(HttpServletRequest.class))).thenReturn(true);

        CreateEmailDataDTO dto = new CreateEmailDataDTO();
        dto.setEmail(TEST_EMAIL);

        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        verify(emailDataService, times(1)).save(dto);
    }

    @Test
    void testSaveEmailData_shouldReturnForbidden_whenOwnershipCheckFails() throws Exception {
        when(ownerShipService.checkOwner(any(HttpServletRequest.class))).thenReturn(false);

        CreateEmailDataDTO dto = new CreateEmailDataDTO();
        dto.setEmail(TEST_EMAIL);

        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(emailDataService, never()).save(dto);
    }

    @Test
    void testSaveEmailData_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        CreateEmailDataDTO dto = new CreateEmailDataDTO();
        dto.setEmail(TEST_EMAIL);

        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}