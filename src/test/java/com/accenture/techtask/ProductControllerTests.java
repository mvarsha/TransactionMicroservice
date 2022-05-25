package com.accenture.techtask;

import org.junit.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class ProductControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void givenActiveStatus_whenProducts_thenStatus200() throws Exception {
        mvc.perform(get("/products?status=ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    public void givenActiveStatus_whenProducts_thenOrderedDescendingStatus200() throws Exception {
        mvc.perform(get("/products?status=ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[3]").doesNotExist());
    }

    @Test
    public void givenInactiveStatus_whenProducts_thenStatus200() throws Exception {
        mvc.perform(get("/products?status=INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    public void givenInactiveStatus_whenProducts_thenOrderedDescendingStatus200() throws Exception {
        mvc.perform(get("/products?status=INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2]").doesNotExist());
    }

    @Test
    public void givenInvalidStatus_whenProducts_thenOrderedDescendingStatus400() throws Exception {
        mvc.perform(get("/products?status=TEST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andDo(print());
    }
}
