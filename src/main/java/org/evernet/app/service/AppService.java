package org.evernet.app.service;

import org.evernet.acl.enums.TargetType;
import org.evernet.acl.model.Access;
import org.evernet.acl.service.AccessService;
import org.evernet.app.model.App;
import org.evernet.app.pojo.AppCreationRequest;
import org.evernet.app.pojo.AppManagerRequest;
import org.evernet.app.pojo.AppUpdateRequest;
import org.evernet.app.repository.AppRepository;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotAllowedException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;
import org.evernet.identity.service.EntityFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
public class AppService {

    private final AppRepository appRepository;

    private final EntityFacadeService entityFacadeService;

    private final AccessService accessService;

    @Autowired
    public AppService(AppRepository appRepository, EntityFacadeService entityFacadeService, AccessService accessService) {
        this.appRepository = appRepository;
        this.entityFacadeService = entityFacadeService;
        this.accessService = accessService;
    }

    public App create(AppCreationRequest appCreationRequest, KnownEntity knownEntity) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (identifierExists(appCreationRequest.getIdentifier())) {
            throw new ClientException("App " + appCreationRequest.getIdentifier() + " already exists");
        }

        App app = new App(appCreationRequest.getIdentifier(),
                appCreationRequest.getDisplayName(),
                appCreationRequest.getDescription(),
                knownEntity.getId());

        app = appRepository.save(app);
        accessService.save(TargetType.APP, app.getIdentifier(), knownEntity, null);

        return app;
    }

    public List<App> list(KnownEntity knownEntity, Pageable pageable) {
        List<Access> mappings = accessService.listTargets(TargetType.APP, knownEntity.getPublicKey(), pageable);
        List<String> appIdentifiers = mappings.stream().map(Access::getTargetIdentifier).toList();
        return get(appIdentifiers);
    }

    public App get(String appIdentifier, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.APP, appIdentifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        return appRepository.findByIdentifier(appIdentifier)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("App not found"));
    }

    public List<App> get(List<String> appIdentifiers) {
        return appRepository.findByIdentifierIn(appIdentifiers);
    }

    public App update(String appIdentifier, AppUpdateRequest appUpdateRequest, KnownEntity knownEntity) throws Throwable {
        App app = get(appIdentifier, knownEntity);

        if (null != appUpdateRequest.getDisplayName() && !appUpdateRequest.getDisplayName().isBlank()) {
            app.setDisplayName(appUpdateRequest.getDisplayName());
        }

        app.setDescription(appUpdateRequest.getDescription());

        app.setModifiedOn(new Date());
        return appRepository.save(app);
    }

    public App delete(String appIdentifier, KnownEntity knownEntity) throws Throwable {
        App app = get(appIdentifier, knownEntity);
        appRepository.delete(app);
        accessService.deleteAll(TargetType.APP, appIdentifier);
        return app;
    }

    public App publish(String appIdentifier, KnownEntity knownEntity) throws Throwable {
        App app = get(appIdentifier, knownEntity);
        app.setPublished(true);
        app.setModifiedOn(new Date());
        return appRepository.save(app);
    }

    public App unPublish(String appIdentifier, KnownEntity knownEntity) throws Throwable {
        App app = get(appIdentifier, knownEntity);
        app.setPublished(false);
        app.setModifiedOn(new Date());
        return appRepository.save(app);
    }

    public Access addManager(String appIdentifier, AppManagerRequest appManagerRequest, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.APP, appIdentifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        EntityDetails entityDetails = entityFacadeService.fetch(appManagerRequest.getEntityNode(), appManagerRequest.getEntityIdentifier());
        return accessService.add(TargetType.APP, appIdentifier, entityDetails, knownEntity.getId());
    }

    public Access deleteManager(String appIdentifier, AppManagerRequest appManagerRequest, KnownEntity knownEntity) throws Throwable {
        if (!accessService.hasAccess(TargetType.APP, appIdentifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        EntityDetails entityDetails = entityFacadeService.fetch(appManagerRequest.getEntityNode(), appManagerRequest.getEntityIdentifier());
        return accessService.delete(TargetType.APP, appIdentifier, entityDetails.getPublicKey().getString());
    }

    public List<Access> listManagers(String appIdentifier, KnownEntity knownEntity, Pageable pageable) {
        if (!accessService.hasAccess(TargetType.APP, appIdentifier, knownEntity.getPublicKey())) {
            throw new NotAllowedException();
        }

        return accessService.list(TargetType.APP, appIdentifier, pageable);
    }

    private Boolean identifierExists(String identifier) {
        return appRepository.existsByIdentifier(identifier);
    }
}
