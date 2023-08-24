package com.dscatalogGSilverio.dscatalog.repositories;

import com.dscatalogGSilverio.dscatalog.entities.Role;
import com.dscatalogGSilverio.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
