package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.StorageSchemaAddress;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.StorageSchema;
import org.evernet.repository.StorageSchemaRepository;
import org.evernet.request.StorageSchemaCreationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageSchemaService {

    private final StorageSchemaRepository storageSchemaRepository;

    private final ConfigService configService;

    private final NodeService nodeService;

    private final RestTemplate restTemplate;

    public StorageSchema create(String nodeIdentifier, StorageSchemaCreationRequest request, String creator) {
        if (StringUtils.hasText(creator)) {
            if (!nodeService.exists(nodeIdentifier)) {
                throw new NotFoundException(String.format("Node %s does not exist", nodeIdentifier));
            }
        }

        StorageSchemaAddress storageSchemaAddress = StorageSchemaAddress.builder()
                .nodeIdentifier(nodeIdentifier)
                .identifier(request.getIdentifier())
                .vertexEndpoint(configService.getVertexEndpoint())
                .build();

        if (storageSchemaRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, storageSchemaAddress.toString())) {
            throw new ClientException(String.format("Storage schema %s already exists on node %s", storageSchemaAddress, nodeIdentifier));
        }

        StorageSchema storageSchema = StorageSchema.builder()
                .nodeIdentifier(nodeIdentifier)
                .address(storageSchemaAddress.toString())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .jsonSchema(request.getJsonSchema())
                .creator(creator)
                .build();

        return storageSchemaRepository.save(storageSchema);
    }

    public List<StorageSchema> list(String nodeIdentifier, Pageable pageable) {
        return storageSchemaRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public StorageSchema get(String nodeIdentifier, String storageSchemaAddress) {
        StorageSchema storageSchema = storageSchemaRepository.findByNodeIdentifierAndAddress(nodeIdentifier, storageSchemaAddress);

        if (storageSchema == null) {
            throw new NotFoundException(String.format("Storage schema %s not found on node %s", storageSchemaAddress, nodeIdentifier));
        }

        return storageSchema;
    }

    public StorageSchema delete(String nodeIdentifier, String storageSchemaAddress) {
        StorageSchema storageSchema = get(nodeIdentifier, storageSchemaAddress);
        storageSchemaRepository.delete(storageSchema);
        return storageSchema;
    }

    public StorageSchema copy(String nodeIdentifier, String storageSchemaAddress, String creator) {
        if (StringUtils.hasText(creator)) {
            if (!nodeService.exists(nodeIdentifier)) {
                throw new NotFoundException(String.format("Node %s does not exist", nodeIdentifier));
            }
        }

        if (storageSchemaRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, storageSchemaAddress)) {
            throw new ClientException(String.format("Storage schema %s already exists on node %s", storageSchemaAddress, nodeIdentifier));
        }

        StorageSchemaAddress address = StorageSchemaAddress.fromString(storageSchemaAddress);

        StorageSchema sourceStorageSchema;

        if (address.getVertexEndpoint().equals(configService.getVertexEndpoint())) {
            sourceStorageSchema = storageSchemaRepository.findByNodeIdentifierAndAddress(nodeIdentifier, address.toString());
        } else {
            String baseUrl = String.format("%s://%s/api/v1/nodes/%s/storage/schema",
                    configService.getFederationProtocol(),
                    address.getVertexEndpoint(),
                    address.getNodeIdentifier());

            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("address", address.toString())
                    .build()
                    .toUriString();

            sourceStorageSchema = restTemplate.getForObject(url, StorageSchema.class);
        }

        if (sourceStorageSchema == null) {
            throw new NotFoundException(String.format("Source storage schema not found for %s", storageSchemaAddress));
        }

        StorageSchema storageSchema = StorageSchema.builder()
                .nodeIdentifier(nodeIdentifier)
                .address(address.toString())
                .displayName(sourceStorageSchema.getDisplayName())
                .description(sourceStorageSchema.getDescription())
                .jsonSchema(sourceStorageSchema.getJsonSchema())
                .creator(sourceStorageSchema.getCreator())
                .build();

        return storageSchemaRepository.save(storageSchema);
    }
}
