package com.dscatalogGSilverio.dscatalog.repositories;

import com.dscatalogGSilverio.dscatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
