package xyz.evernet.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.admin.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

}
