package com.dscatalogGSilverio.dscatalog.repositories;

import com.dscatalogGSilverio.dscatalog.entities.Product;
import com.dscatalogGSilverio.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {
    private long existingId;
    private long notExistingId;
    private long countTotalProducts = 25L;
    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        notExistingId = 26L;
    }
    @Autowired
    private ProductRepository repository;

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIdNull(){

        Product product = Factory.createProduct();
        product.setId(null);
        product =  repository.save(product);
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts+1,product.getId());
    }
    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){

        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());

    }
    @Test
    public void findByIdShouldReturnOptionalNotEmptyWhenIdExist()
    {
        repository.findById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }
    @Test
    public void findByIdShouldReturnOptionalEmptyWhenIdNotExist()
    {
        repository.findById(notExistingId);
        Optional<Product> result = repository.findById(notExistingId);
        Assertions.assertFalse(result.isPresent());
    }
}
