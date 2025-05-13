package com.backend.bankingapplication.controller;

import com.backend.bankingapplication.app.dto.create.CreateEmailDataDTO;
import com.backend.bankingapplication.app.repository.EmailDataRepository;
import com.backend.bankingapplication.base.BaseIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class EmailDataControllerIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_PATH = "/emails";

    private final CreateEmailDataDTO correctEmailData = new CreateEmailDataDTO("test@email.com");
    private final CreateEmailDataDTO incorrectEmailData = new CreateEmailDataDTO("testemail.com");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailDataRepository emailDataRepository;

    @BeforeEach
    public void cleanup() {
        emailDataRepository.deleteAll();
    }

    @Test
    @WithAuthUser
    void shouldSuccessSave() throws Exception {
        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctEmailData)))
                .andExpect(status().isCreated());

        boolean existsByEmail = emailDataRepository.existsByEmail(correctEmailData.getEmail());
        assertTrue(existsByEmail);
    }

    @Test
    @WithAuthUser
    void shouldBadRequest_whenSendingEmailDataTwice() throws Exception {
        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctEmailData)))
                .andExpect(status().isCreated());

        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctEmailData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAuthUser
    void shouldThrowBadRequest_whenSendIncorrectEmailData() throws Exception {
        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectEmailData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowUnauthorized_whenEmptySecurityContext() throws Exception {
        mockMvc.perform(post(TEST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctEmailData)))
                .andExpect(status().isUnauthorized());
    }
}