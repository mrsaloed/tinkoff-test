package com.example.tinkofftest.controllers;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class TranslateControllerTest {

    private static final String CORRECT_MESSAGE = "Hi Tinkoff!";
    private static final String CORRECT_PARAMS = "en-ru";
    private static final String INCORRECT_PARAMS = "1";
    private static final String EMPTY_MESSAGE = "";
    private static final String MESSAGE_STRING = "message";
    private static final String ERROR_STRING = "error";
    private static final String URL_TEMPLATE = "/";

    @Autowired
    MockMvc mvc;

    @Test
    void shouldPassNormally_translate() throws Exception {
        mvc.perform(post(getCorrectMessageBody()))
                .andExpect(getCorrectResponse());
    }

    @Test
    void shouldReturnError_translate()throws Exception {
        mvc.perform(post(getIncorrectMessageBody()))
                .andExpect(getIncorrectResponse());

        mvc.perform(post(getIncorrectParamBody()))
                .andExpect(getIncorrectResponse());
    }

    private MockHttpServletRequestBuilder post(Object content) {
        return MockMvcRequestBuilders
                .post(URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(content));
    }

    private ResultMatcher getCorrectResponse() {
        return MockMvcResultMatchers.content().string(containsString(MESSAGE_STRING));
    }

    private ResultMatcher getIncorrectResponse() {
        return MockMvcResultMatchers.content().string(containsString(ERROR_STRING));
    }

    private MessageToTranslateBody getCorrectMessageBody() {
        MessageToTranslateBody message = new MessageToTranslateBody();
        message.setMessage(CORRECT_MESSAGE);
        message.setParameters(CORRECT_PARAMS);
        return message;
    }

    private MessageToTranslateBody getIncorrectMessageBody() {
        MessageToTranslateBody message = new MessageToTranslateBody();
        message.setMessage(EMPTY_MESSAGE);
        message.setParameters(CORRECT_PARAMS);
        return message;
    }

    private Object getIncorrectParamBody() {
        MessageToTranslateBody message = new MessageToTranslateBody();
        message.setMessage(CORRECT_MESSAGE);
        message.setParameters(INCORRECT_PARAMS);
        return message;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}