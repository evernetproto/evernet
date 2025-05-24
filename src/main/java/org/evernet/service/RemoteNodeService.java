package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.NodeAddress;
import org.evernet.model.Message;
import org.evernet.model.Node;
import org.evernet.request.MessageReceiverRequest;
import org.evernet.response.MessageReceiverResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteNodeService {

    private final RestTemplate restTemplate;

    private final ConfigService configService;

    public Node getNode(String nodeIdentifier, String vertexEndpoint) {
        return restTemplate.getForObject(String.format("%s://%s/api/v1/nodes/%s",
                configService.getFederationProtocol(),
                vertexEndpoint,
                nodeIdentifier
        ), Node.class);
    }

    public MessageReceiverResponse sendMessages(List<Message> messages, NodeAddress recipientNode, NodeAddress senderNode, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        MessageReceiverRequest requestPayload = MessageReceiverRequest.builder()
                .messages(messages)
                .build();

        HttpEntity<MessageReceiverRequest> requestEntity = new HttpEntity<>(requestPayload, headers);

        return restTemplate.postForObject(
                String.format(
                        "%s://%s/api/v1/messaging/receive",
                        configService.getFederationProtocol(),
                        recipientNode.getVertexEndpoint()
                ),
                requestEntity,
                MessageReceiverResponse.class
        );
    }
}
