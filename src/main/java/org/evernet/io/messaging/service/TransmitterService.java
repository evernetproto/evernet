package org.evernet.io.messaging.service;

import org.evernet.acl.enums.TargetType;
import org.evernet.acl.model.Access;
import org.evernet.acl.service.AccessService;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotAllowedException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;
import org.evernet.identity.service.EntityFacadeService;
import org.evernet.io.messaging.model.Transmitter;
import org.evernet.io.messaging.pojo.TransmitterAccessRequest;
import org.evernet.io.messaging.pojo.TransmitterCreationRequest;
import org.evernet.io.messaging.pojo.TransmitterUpdateRequest;
import org.evernet.io.messaging.repository.TransmitterRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
public class TransmitterService {

    private final TransmitterRepository transmitterRepository;

    private final EntityFacadeService entityFacadeService;

    private final AccessService accessService;

    public TransmitterService(TransmitterRepository transmitterRepository, EntityFacadeService entityFacadeService, AccessService accessService) {
        this.transmitterRepository = transmitterRepository;
        this.entityFacadeService = entityFacadeService;
        this.accessService = accessService;
    }

    public Transmitter create(TransmitterCreationRequest transmitterCreationRequest, KnownEntity knownEntity) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (identifierExists(transmitterCreationRequest.getIdentifier())) {
            throw new ClientException("Transmitter " + transmitterCreationRequest.getIdentifier() + " already exists");
        }

        Transmitter transmitter = new Transmitter(transmitterCreationRequest.getIdentifier(),
                transmitterCreationRequest.getDisplayName(),
                transmitterCreationRequest.getDescription(),
                knownEntity.getId());

        transmitter = transmitterRepository.save(transmitter);
        accessService.save(TargetType.TRANSMITTER, transmitter.getIdentifier(), knownEntity, null);

        return transmitter;
    }

    public List<Transmitter> list(KnownEntity knownEntity, Pageable pageable) {
        List<Access> mappings = accessService.listTargets(TargetType.TRANSMITTER, knownEntity.getPublicKey(), pageable);
        List<String> transmitterIdentifiers = mappings.stream().map(Access::getTargetIdentifier).toList();
        return get(transmitterIdentifiers);
    }

    public Transmitter get(String identifier, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.TRANSMITTER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        return transmitterRepository.findByIdentifier(identifier)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Transmitter not found"));
    }

    public List<Transmitter> get(List<String> transmitterIdentifiers) {
        return transmitterRepository.findByIdentifierIn(transmitterIdentifiers);
    }

    public Transmitter update(String identifier, TransmitterUpdateRequest transmitterUpdateRequest, KnownEntity knownEntity) throws Throwable {
        Transmitter transmitter = get(identifier, knownEntity);

        if (null != transmitterUpdateRequest.getDisplayName() && !transmitterUpdateRequest.getDisplayName().isBlank()) {
            transmitter.setDisplayName(transmitterUpdateRequest.getDisplayName());
        }

        transmitter.setDescription(transmitterUpdateRequest.getDescription());

        transmitter.setModifiedOn(new Date());
        return transmitterRepository.save(transmitter);
    }

    public Transmitter delete(String identifier, KnownEntity knownEntity) throws Throwable {
        Transmitter transmitter = get(identifier, knownEntity);
        transmitterRepository.delete(transmitter);
        accessService.deleteAll(TargetType.TRANSMITTER, identifier);
        return transmitter;
    }

    public Access addAccess(String identifier, TransmitterAccessRequest transmitterAccessRequest, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.TRANSMITTER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        EntityDetails entityDetails = entityFacadeService.fetch(transmitterAccessRequest.getEntityNode(), transmitterAccessRequest.getEntityIdentifier());
        return accessService.add(TargetType.TRANSMITTER, identifier, entityDetails, knownEntity.getId());
    }

    public Access deleteAccess(String identifier, TransmitterAccessRequest transmitterAccessRequest, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.TRANSMITTER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        EntityDetails entityDetails = entityFacadeService.fetch(transmitterAccessRequest.getEntityNode(), transmitterAccessRequest.getEntityIdentifier());
        return accessService.delete(TargetType.TRANSMITTER, identifier, entityDetails.getPublicKey().getString());
    }

    public List<Access> listAccess(String identifier, KnownEntity knownEntity, Pageable pageable) {
        if (!accessService.hasAccess(TargetType.TRANSMITTER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        return accessService.list(TargetType.TRANSMITTER, identifier, pageable);
    }

    private Boolean identifierExists(String identifier) {
        return transmitterRepository.existsByIdentifier(identifier);
    }
}
