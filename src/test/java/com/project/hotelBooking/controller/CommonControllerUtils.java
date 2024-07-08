package com.project.hotelBooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Component
@RequiredArgsConstructor
public class CommonControllerUtils {

    private final MockMvc mockMvc;

    public ResultActions performPostRequest(String url, String jsonContent) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

}
