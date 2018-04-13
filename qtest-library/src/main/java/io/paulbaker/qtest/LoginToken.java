package io.paulbaker.qtest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Value;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Paul N. Baker on 04/13/2018
 */
@Value
@JsonDeserialize(using = LoginToken.LoginTokenDeserializer.class)
public class LoginToken implements Authenticator {

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private Set<String> scope;
    private String agent;

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return null;
    }

    public static class LoginTokenDeserializer extends StdDeserializer<LoginToken> {

        public LoginTokenDeserializer() {
            this(null);
        }

        public LoginTokenDeserializer(Class<?> valueClass) {
            super(valueClass);
        }

        @Override
        public LoginToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            // This circular logic is strange, but how Jackson works in this context.
            JsonNode treeNode = p.getCodec().readTree(p);
            String accessToken = asNullableText(treeNode.get("access_token"));
            String tokenType = asNullableText(treeNode.get("token_type"));
            String refreshToken = asNullableText(treeNode.get("refresh_token"));
            String scope = Optional.ofNullable(treeNode.get("scope")).map(JsonNode::asText).orElse("");
            Set<String> itemizedScope = Arrays.stream(scope.split("\\s+")).collect(Collectors.toSet());
            String agent = asNullableText(treeNode.get("agent"));
            return new LoginToken(accessToken, tokenType, refreshToken, itemizedScope, agent);
        }

        private String asNullableText(JsonNode jsonNode) {
            if (jsonNode == null) {
                return null;
            }
            return jsonNode.asText(null);
        }
    }
}
