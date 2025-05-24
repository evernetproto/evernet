package org.evernet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evernet.converter.MapToJsonConverter;
import org.evernet.converter.ObjectToJsonConverter;
import org.evernet.enums.MessageStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@Table(name = "messages")
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String senderMessageGatewayIdentifier;

    private String senderActorAddress;

    private String senderNodeIdentifier;

    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> topic;

    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> headers;

    @Convert(converter = ObjectToJsonConverter.class)
    private Object payload;

    private String recipientMessageGatewayIdentifier;

    private String recipientNodeIdentifier;

    private String recipientVertexEndpoint;

    private Instant sendAt;

    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @CreationTimestamp
    private Instant createdAt;
}
