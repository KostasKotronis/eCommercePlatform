package com.publicNext.eCommercePlatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @Order(1)
    void createOrder_returnsCreatedOrder() throws Exception {
        String body = """
          {
            "customerName": "Test Customer Name",
            "lines": [
              { "productId": 101, "quantity": 2, "price": 19.90 }
            ]
          }
        """;

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Test Customer Name"))
                .andExpect(jsonPath("$.lines[0].productId").value(101))
                .andExpect(jsonPath("$.status").value("unprocessed"));
    }

    @Test
    @Order(2)
    void createOrder_withInvalidFields_returnsBadRequest() throws Exception {
        String badBody = """
          {
            "customerName": "",
            "lines": [
              { "productId": null, "quantity": -1, "price": -5.00 }
            ]
          }
        """;

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.customerName").exists());
    }

    @Test
    @Order(3)
    void getOrderById_returnsOrder() throws Exception {
        String body = """
          {
            "customerName": "Test Customer Name",
            "lines": [
              { "productId": 202, "quantity": 1, "price": 10.00 }
            ]
          }
        """;

        String json = mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(json).get("id").asLong();

        mvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerName").value("Test Customer Name"));
    }


    @Test
    @Order(4)
    void updateOrder_replacesLines_andKeepsStatus() throws Exception {
        String createBody = """
          {
            "customerName": "Test Customer Name",
            "lines": [
              { "productId": 301, "quantity": 1, "price": 15.00 }
            ]
          }
        """;

        String json = mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(json).get("id").asLong();

        String updateBody = """
          {
            "customerName": "Test Customer Name Updated",
            "lines": [
              { "productId": 302, "quantity": 3, "price": 20.00 }
            ]
          }
        """;

        mvc.perform(put("/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Test Customer Name Updated"))
                .andExpect(jsonPath("$.lines[0].productId").value(302))
                .andExpect(jsonPath("$.status").value("unprocessed")); // status unchanged
    }

    @Test
    @Order(5)
    void deleteOrder_removesItFromDatabase() throws Exception {
        String body = """
          {
            "customerName": "Test Customer Name",
            "lines": [
              { "productId": 999, "quantity": 1, "price": 9.99 }
            ]
          }
        """;

        String json = mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(json).get("id").asLong();

        mvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/orders/{id}", id))
                .andExpect(status().isNotFound());
    }
}