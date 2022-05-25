package com.accenture.techtask;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
class TransactionControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void givenInvalidDate_whenAddTransactions_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"customerId\": 10003, \"quantity\": 2, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2022-05-25 16:43\"}]"))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-001\",\"message\":\"Invalid transaction date\"}"));
    }

    @Test
    public void givenInvalidFormatDate_whenAddTransactions_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10003, \"quantity\": 2, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"20-05-25 16:43\"}]"))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-001\",\"message\":\"Invalid transaction date\"}"));
    }

    @Test
    public void givenInactiveProduct_whenAddTransactions_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10003, \"quantity\": 2, \"productCode\": \"PRODUCT_004\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-002\",\"message\":\"No valid transactions\"}"));
    }

    @Test
    public void givenInvalidProduct_whenAddTransactions_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10003, \"quantity\": 2, \"productCode\": \"PRODUCT_111\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-002\",\"message\":\"No valid transactions\"}"));
    }

    @Test
    public void givenInvalidCustomer_whenAddTransactions_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 123, \"quantity\": 2, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-002\",\"message\":\"No valid transactions\"}"));
    }

    @Test
    public void givenExcessTotalCost_whenAddTransactions_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 11, \"productCode\": \"PRODUCT_005\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-003\",\"message\":\"Cost exceeds 5000\"}"));
    }

    @Test
    public void givenValidTransaction_whenAddTransactions_thenStatus200() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 10, \"productCode\": \"PRODUCT_005\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productCode").value("PRODUCT_005"));
    }

    @Test
    public void givenSomeValidTransactions_whenAddTransactions_thenStatus200() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 2, \"productCode\": \"PRODUCT_002\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10001, \"quantity\": 10, \"productCode\": \"PRODUCT_005\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productCode").value("PRODUCT_005"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").doesNotExist());
    }

    @Test
    public void givenAllValidTransactions_whenAddTransactions_thenStatus200() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10001, \"quantity\": 5, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].productCode").isNotEmpty());
    }

    @Test
    public void givenCustomer_whenCost_thenStatus200() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10005, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10005, \"quantity\": 5, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transaction/cost/customer/10005")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("350"));
    }

    @Test
    public void givenInvalidCustomer_whenCost_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10001, \"quantity\": 5, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transaction/cost/customer/1000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-002\",\"message\":\"No valid transactions\"}"));
    }

    @Test
    public void givenProductCode_whenCost_thenStatus200() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10001, \"quantity\": 5, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transaction/cost/product/PRODUCT_003")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("1000"));
    }

    @Test
    public void givenInvalidProductCode_whenCost_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10001, \"quantity\": 5, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transaction/cost/product/PRODUCT_145")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-002\",\"message\":\"No valid transactions\"}"));
    }

    @Test
    public void givenCountryCode_whenTotal_thenStatus200() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10002, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10004, \"quantity\": 5, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transaction/count?country=US")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("2"));
    }

    @Test
    public void givenInvalidCountryCode_whenCost_thenStatus400() throws Exception {
        mvc.perform(post("/transaction/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"customerId\": 10001, \"quantity\": 2, \"productCode\": \"PRODUCT_001\", \"transactionTime\": \"2023-05-20 16:43\"}," +
                                "{\"customerId\": 10001, \"quantity\": 5, \"productCode\": \"PRODUCT_003\", \"transactionTime\": \"2023-05-25 16:43\"}]"))
                .andExpect(status().is(201))
                .andDo(print());
        mvc.perform(get("/transaction/count?country=AND")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("{\"error\":\"e-002\",\"message\":\"No valid transactions\"}"));
    }
}
