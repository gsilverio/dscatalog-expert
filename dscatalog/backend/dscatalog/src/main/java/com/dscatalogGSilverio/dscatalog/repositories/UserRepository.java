package com.dscatalogGSilverio.dscatalog.repositories;

import com.dscatalogGSilverio.dscatalog.entities.Product;
import com.dscatalogGSilverio.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}