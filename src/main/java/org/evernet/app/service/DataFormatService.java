package org.evernet.app.service;

import org.evernet.acl.enums.TargetType;
import org.evernet.acl.model.Access;
import org.evernet.acl.service.AccessService;
import org.evernet.app.model.DataFormat;
import org.evernet.app.pojo.DataFormatCreationRequest;
import org.evernet.app.pojo.DataFormatUpdateRequest;
import org.evernet.app.repository.DataFormatRepository;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotAllowedException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.identity.model.KnownEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DataFormatService {

    private final DataFormatRepository dataFormatRepository;

    private final AccessService accessService;

    @Autowired
    public DataFormatService(DataFormatRepository dataFormatRepository, AccessService accessService) {
        this.dataFormatRepository = dataFormatRepository;
        this.accessService = accessService;
    }

    public DataFormat register(DataFormatCreationRequest dataFormatCreationRequest, KnownEntity knownEntity) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (identifierExists(dataFormatCreationRequest.getIdentifier())) {
            throw new ClientException("Data format " + dataFormatCreationRequest.getIdentifier() + " already exists");
        }

        if (null != dataFormatCreationRequest.getAppIdentifier() && !dataFormatCreationRequest.getAppIdentifier().isBlank()) {
            if (!accessService.hasAccess(TargetType.APP, dataFormatCreationRequest.getIdentifier(), knownEntity.getPublicKey())) {
                throw new NotAllowedException();
            }
        }

        DataFormat dataFormat = new DataFormat(dataFormatCreationRequest.getIdentifier(),
                dataFormatCreationRequest.getDisplayName(),
                dataFormatCreationRequest.getDescription(),
                dataFormatCreationRequest.getDataSchema(),
                dataFormatCreationRequest.getAppIdentifier(),
                knownEntity.getId());

        accessService.save(TargetType.DATA_FORMAT, dataFormat.getIdentifier(), knownEntity, null);
        return dataFormat;
    }

    public DataFormat delete(String dataFormatIdentifier, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.DATA_FORMAT, dataFormatIdentifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        DataFormat dataFormat = get(dataFormatIdentifier, knownEntity);
        dataFormatRepository.delete(dataFormat);

        return dataFormat;
    }

    public List<DataFormat> list(KnownEntity knownEntity, Pageable pageable) {
        List<Access> mappings = accessService.listTargets(TargetType.DATA_FORMAT, knownEntity.getPublicKey(), pageable);
        List<String> identifiers = mappings.stream().map(Access::getTargetIdentifier).toList();
        return get(identifiers);
    }

    public DataFormat get(String dataFormatIdentifier, KnownEntity knownEntity) throws Throwable {
        return dataFormatRepository.findByIdentifier(dataFormatIdentifier)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Data format not found"));
    }

    public List<DataFormat> get(List<String> identifiers) {
        return dataFormatRepository.findByIdentifierIn(identifiers);
    }

    public DataFormat update(String identifier, DataFormatUpdateRequest dataFormatUpdateRequest, KnownEntity knownEntity) throws Throwable {
        DataFormat dataFormat = get(identifier, knownEntity);

        if (null != dataFormatUpdateRequest.getDisplayName() && !dataFormatUpdateRequest.getDisplayName().isBlank()) {
            dataFormat.setDisplayName(dataFormatUpdateRequest.getDisplayName());
        }

        dataFormat.setDescription(dataFormatUpdateRequest.getDescription());

        if (null != dataFormatUpdateRequest.getDataSchema()) {
            dataFormat.setDataSchema(dataFormatUpdateRequest.getDataSchema());
        }

        dataFormat.setModifiedOn(new Date());
        return dataFormatRepository.save(dataFormat);
    }

    private Boolean identifierExists(String identifier) {
        return dataFormatRepository.existsByIdentifier(identifier);
    }
}
