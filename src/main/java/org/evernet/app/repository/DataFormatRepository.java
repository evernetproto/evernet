package org.evernet.app.repository;

import org.evernet.app.model.DataFormat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataFormatRepository extends MongoRepository<DataFormat, String> {

    Optional<DataFormat> findByIdentifier(String identifier);

    List<DataFormat> findByIdentifierIn(List<String> identifiers);

    Boolean existsByIdentifier(String identifier);
}
