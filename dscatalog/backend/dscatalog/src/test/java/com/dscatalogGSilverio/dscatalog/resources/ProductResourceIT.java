package com.dscatalogGSilverio.dscatalog.resources;

import com.dscatalogGSilverio.dscatalog.dto.ProductDTO;
import com.dscatalogGSilverio.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId=26L;
        countTotalProducts = 25L;
    }
    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isOk());
        mockMvc.perform(builder).andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        mockMvc.perform(builder).andExpect(jsonPath("$.content").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        mockMvc.perform(builder).andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        mockMvc.perform(builder).andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        ProductDTO productDTO = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/products/{id}", existingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder).andExpect(status().isOk());
        mockMvc.perform(builder).andExpect(jsonPath("$.id").value(existingId));
        mockMvc.perform(builder).andExpect(jsonPath("$.name").value(expectedName));
        mockMvc.perform(builder).andExpect(jsonPath("$.description").value(expectedDescription));

    }
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ProductDTO productDTO = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDTO);


        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder).andExpect(status().isNotFound());
    }

}
