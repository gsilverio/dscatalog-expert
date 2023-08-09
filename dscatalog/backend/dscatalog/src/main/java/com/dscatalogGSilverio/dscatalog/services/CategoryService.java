package com.dscatalogGSilverio.dscatalog.services;

import com.dscatalogGSilverio.dscatalog.entities.Category;
import com.dscatalogGSilverio.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll(){
        return repository.findAll();
    }
}
