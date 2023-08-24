package com.dscatalogGSilverio.dscatalog.services;

import com.dscatalogGSilverio.dscatalog.dto.ProductDTO;
import com.dscatalogGSilverio.dscatalog.entities.Category;
import com.dscatalogGSilverio.dscatalog.entities.Product;
import com.dscatalogGSilverio.dscatalog.repositories.CategoryRepository;
import com.dscatalogGSilverio.dscatalog.repositories.ProductRepository;
import com.dscatalogGSilverio.dscatalog.services.exceptions.DatabaseException;
import com.dscatalogGSilverio.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalogGSilverio.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServicesTests {
    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private long existingId;
    private long dependentId;
    private PageImpl<Product> page;
    private ProductDTO productDTO;
    private Product product;
    private long nonExistingId;
    @BeforeEach
    void setUp() throws Exception {
        productDTO = Factory.createProductDto();
        category = Factory.createCategory();
        dependentId = 3L;
        existingId = 1L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));


        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);

    }
    @Test
    public void deleteShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(()->{
            service.delete(existingId);
        });

    }
    @Test
    public void deleteShouldThrowResourceNotFoundWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.delete(nonExistingId);
        });

    }
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(DatabaseException.class,()->{
            service.delete(dependentId);
        });

    }
    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0,10);
        Page<ProductDTO> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }
    @Test
    public void findByIdShouldReturnProductDto(){

        ProductDTO result = service.findById(existingId);
        Assertions.assertNotNull(result);

    }
    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.findById(nonExistingId);
        });

    }
    @Test
    public void updateShouldReturnProductDTOWhenIdExists(){

        ProductDTO result = service.update(existingId,productDTO);
        Assertions.assertNotNull(result);
    }
    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.update(nonExistingId,productDTO);
        });

    }
}
