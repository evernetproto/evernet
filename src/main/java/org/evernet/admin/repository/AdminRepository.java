package org.evernet.admin.repository;

import org.evernet.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByIdentifier(String identifier);

    Boolean existsByIdentifier(String identifier);
}
