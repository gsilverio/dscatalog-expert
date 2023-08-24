package com.dscatalogGSilverio.dscatalog.resources;

import com.dscatalogGSilverio.dscatalog.dto.ProductDTO;
import com.dscatalogGSilverio.dscatalog.services.ProductService;
import com.dscatalogGSilverio.dscatalog.services.exceptions.DatabaseException;
import com.dscatalogGSilverio.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalogGSilverio.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Long dependentId;
    private PageImpl<ProductDTO> page;
    private ProductDTO productDTO;
    private long existingId;
    private long nonExistingId;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        productDTO = Factory.createProductDto();
        page = new PageImpl<>(List.of(productDTO));

        when(service.findAllPaged(any())).thenReturn(page);
        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any())).thenReturn(productDTO);

        when(service.update(eq(existingId), any())).thenReturn(productDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DatabaseException.class).when(service).delete(dependentId);

    }
    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isNoContent());



    }
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isNotFound());

    }
    @Test
    public void insertShouldReturnProductDTO() throws Exception{

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/products")
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder).andExpect(status().isCreated());
        mockMvc.perform(builder).andExpect(jsonPath("$.id").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.name").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.description").exists());


    }

    @Test
    public void findAllShouldReturnPage() throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/products").accept(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(builder).andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isOk());
        mockMvc.perform(builder).andExpect(jsonPath("$.id").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.name").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.description").exists());
    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists()throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isNotFound());

    }
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/products/{id}", existingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder).andExpect(status().isOk());
        mockMvc.perform(builder).andExpect(jsonPath("$.id").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.name").exists());
        mockMvc.perform(builder).andExpect(jsonPath("$.description").exists());

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);



        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder).andExpect(status().isNotFound());

    }

}
