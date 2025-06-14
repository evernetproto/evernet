package org.evernet.repository;

import org.evernet.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, String> {

    boolean existsByKey(String key);

    Config findByKey(String key);
}