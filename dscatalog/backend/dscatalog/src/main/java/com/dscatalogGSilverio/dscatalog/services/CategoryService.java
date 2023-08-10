package com.dscatalogGSilverio.dscatalog.services;

import com.dscatalogGSilverio.dscatalog.dto.CategoryDTO;
import com.dscatalogGSilverio.dscatalog.entities.Category;
import com.dscatalogGSilverio.dscatalog.repositories.CategoryRepository;
import com.dscatalogGSilverio.dscatalog.services.exceptions.DatabaseException;
import com.dscatalogGSilverio.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable){
        Page<Category> list = repository.findAll(pageable);
        /* DIFERENTE DO LIST, O PAGE JÁ É UMA STREAM ENTÃO NÃO É NECESSARIO CONVERTER ELE E DEPOIS RECONVERTER COM O COLLECT
        List<CategoryDTO>listDTO = list.stream().map(x->new CategoryDTO(x)).collect(Collectors.toList());*/
        Page<CategoryDTO>listDTO = list.map(x->new CategoryDTO(x));
        return listDTO;
    }
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(()->new ResourceNotFoundException("Entity not found"));
        return new CategoryDTO(entity);

    }
    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return new CategoryDTO(entity);

        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found"+id);
        }

    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}
