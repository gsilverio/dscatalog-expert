package com.dscatalogGSilverio.dscatalog.repositories;

import com.dscatalogGSilverio.dscatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
