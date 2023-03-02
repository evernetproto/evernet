package org.evernet.app.repository;

import org.evernet.app.model.App;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppRepository extends MongoRepository<App, String> {

    Optional<App> findByIdentifier(String identifier);

    List<App> findByIdentifierIn(List<String> identifiers);

    Boolean existsByIdentifier(String identifier);
}
