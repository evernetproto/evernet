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
import org.evernet.io.messaging.model.Receiver;
import org.evernet.io.messaging.pojo.ReceiverAccessRequest;
import org.evernet.io.messaging.pojo.ReceiverCreationRequest;
import org.evernet.io.messaging.pojo.ReceiverUpdateRequest;
import org.evernet.io.messaging.repository.ReceiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ReceiverService {

    private final ReceiverRepository receiverRepository;

    private final EntityFacadeService entityFacadeService;

    private final AccessService accessService;

    @Autowired
    public ReceiverService(ReceiverRepository receiverRepository, EntityFacadeService entityFacadeService, AccessService accessService) {
        this.receiverRepository = receiverRepository;
        this.entityFacadeService = entityFacadeService;
        this.accessService = accessService;
    }

    public Receiver create(ReceiverCreationRequest receiverCreationRequest, KnownEntity knownEntity) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (identifierExists(receiverCreationRequest.getIdentifier())) {
            throw new ClientException("Receiver " + receiverCreationRequest.getIdentifier() + " already exists");
        }

        Receiver receiver = new Receiver(receiverCreationRequest.getIdentifier(),
                receiverCreationRequest.getDisplayName(),
                receiverCreationRequest.getDescription(),
                knownEntity.getId());

        receiver = receiverRepository.save(receiver);
        accessService.save(TargetType.RECEIVER, receiver.getIdentifier(), knownEntity, null);

        return receiver;
    }

    public List<Receiver> list(KnownEntity knownEntity, Pageable pageable) {
        List<Access> mappings = accessService.listTargets(TargetType.RECEIVER, knownEntity.getPublicKey(), pageable);
        List<String> receiverIdentifiers = mappings.stream().map(Access::getTargetIdentifier).toList();
        return get(receiverIdentifiers);
    }

    public Receiver get(String identifier, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.RECEIVER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        return receiverRepository.findByIdentifier(identifier)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Receiver not found"));
    }

    public List<Receiver> get(List<String> receiverIdentifiers) {
        return receiverRepository.findByIdentifierIn(receiverIdentifiers);
    }

    public Receiver update(String identifier, ReceiverUpdateRequest receiverUpdateRequest, KnownEntity knownEntity) throws Throwable {
        Receiver receiver = get(identifier, knownEntity);

        if (null != receiverUpdateRequest.getDisplayName() && !receiverUpdateRequest.getDisplayName().isBlank()) {
            receiver.setDisplayName(receiverUpdateRequest.getDisplayName());
        }

        receiver.setDescription(receiverUpdateRequest.getDescription());

        receiver.setModifiedOn(new Date());
        return receiverRepository.save(receiver);
    }

    public Receiver delete(String identifier, KnownEntity knownEntity) throws Throwable {
        Receiver receiver = get(identifier, knownEntity);
        receiverRepository.delete(receiver);
        accessService.deleteAll(TargetType.RECEIVER, identifier);
        return receiver;
    }

    public Access addAccess(String identifier, ReceiverAccessRequest receiverAccessRequest, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.RECEIVER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        EntityDetails entityDetails = entityFacadeService.fetch(receiverAccessRequest.getEntityNode(), receiverAccessRequest.getEntityIdentifier());
        return accessService.add(TargetType.RECEIVER, identifier, entityDetails, knownEntity.getId());
    }

    public Access deleteAccess(String identifier, ReceiverAccessRequest receiverAccessRequest, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.RECEIVER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        EntityDetails entityDetails = entityFacadeService.fetch(receiverAccessRequest.getEntityNode(), receiverAccessRequest.getEntityIdentifier());
        return accessService.delete(TargetType.RECEIVER, identifier, entityDetails.getPublicKey().getString());
    }

    public List<Access> listAccess(String identifier, KnownEntity knownEntity, Pageable pageable) {
        if (!accessService.hasAccess(TargetType.RECEIVER, identifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        return accessService.list(TargetType.RECEIVER, identifier, pageable);
    }

    private Boolean identifierExists(String identifier) {
        return receiverRepository.existsByIdentifier(identifier);
    }
}
