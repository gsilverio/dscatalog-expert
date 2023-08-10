package com.dscatalogGSilverio.dscatalog.services;

import com.dscatalogGSilverio.dscatalog.dto.ProductDTO;
import com.dscatalogGSilverio.dscatalog.entities.Product;
import com.dscatalogGSilverio.dscatalog.repositories.ProductRepository;
import com.dscatalogGSilverio.dscatalog.services.exceptions.DatabaseException;
import com.dscatalogGSilverio.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
        Page<Product> list = repository.findAll(pageRequest);
        /* DIFERENTE DO LIST, O PAGE JÁ É UMA STREAM ENTÃO NÃO É NECESSARIO CONVERTER ELE E DEPOIS RECONVERTER COM O COLLECT
        List<ProductDTO>listDTO = list.stream().map(x->new ProductDTO(x)).collect(Collectors.toList());*/
        Page<ProductDTO>listDTO = list.map(x->new ProductDTO(x));
        return listDTO;
    }
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(()->new ResourceNotFoundException("Entity not found"));
        return new ProductDTO(entity, entity.getCategories());

    }
    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();
        //entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }
    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            //entity.setName(dto.getName());
            entity = repository.save(entity);
            return new ProductDTO(entity);

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
