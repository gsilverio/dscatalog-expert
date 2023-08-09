package com.dscatalogGSilverio.dscatalog.resources;
/* Resource = Controllers */

import com.dscatalogGSilverio.dscatalog.entities.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/categories")
public class CategoryResource {
    @GetMapping
    public ResponseEntity<List<Category>> findAll(){
        List<Category> list = new ArrayList<>();
        list.add(new Category(1L,"books"));
        list.add(new Category(2L,"eletronicos"));
        return ResponseEntity.ok().body(list);
    }
}
