package org.evernet.identity.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.evernet.common.exception.InvalidTokenException;
import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;
import org.evernet.identity.service.KnownEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenParser tokenParser;

    private final String nodeEndpoint;

    private final KnownEntityService knownEntityService;

    @Autowired
    public AuthenticationInterceptor(TokenParser tokenParser,
                                     KnownEntityService knownEntityService,
                                     @Value("${evernet.node.endpoint}") String nodeEndpoint) {
        this.tokenParser = tokenParser;
        this.nodeEndpoint = nodeEndpoint;
        this.knownEntityService = knownEntityService;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            Object handlerBean = ((HandlerMethod) handler).getBean();

            if (handlerBean instanceof RequiresAuthentication) {
                String jwtToken = extractToken(request);

                Token token = tokenParser.parse(jwtToken);

                if (null == token) {
                    throw new InvalidTokenException();
                }

                if (!token.getAudience().equals(nodeEndpoint)) {
                    throw new InvalidTokenException();
                }

                EntityDetails entityDetails = ThreadLocalWrapper.getAuthenticatedEntity();

                if (null == entityDetails) {
                    throw new InvalidTokenException();
                }

                if (!token.getIdentifier().equals(entityDetails.getIdentifier())) {
                    throw new InvalidTokenException();
                }

                KnownEntity knownEntity;

                try {
                    knownEntity = knownEntityService.register(entityDetails);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }

                ThreadLocalWrapper.setKnownEntity(knownEntity);
            }
        }

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null) {
            throw new InvalidTokenException();
        }

        String[] authorizationComponents = authorizationHeader.split("\\s+");

        if (authorizationComponents.length != 2) {
            throw new InvalidTokenException();
        }

        if (!authorizationComponents[0].equals("Bearer")) {
            throw new InvalidTokenException();
        }

        return authorizationComponents[1];
    }
}
