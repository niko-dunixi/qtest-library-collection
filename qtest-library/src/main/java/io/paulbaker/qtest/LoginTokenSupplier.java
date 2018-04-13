package io.paulbaker.qtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Created by Paul N. Baker on 04/13/2018
 * <p>
 * Retrieves a login token.
 * {@see https://api.qasymphony.com/#/login/postAccessToken}
 */
public class LoginTokenSupplier implements Supplier<LoginToken> {

    private final String subdomain;
    private final String username;
    private final char[] password;

    /**
     * @param subdomain Your sub-domain on qtestnet. For instance, if your qtest url is "xyz.qtestnet.com" your subdomain is "xyz"
     * @param username  The account you will login with
     * @param password  The password to use for this login. We are using a char[] to prevent memory leaking credentials since these are held in memory for the lifetime of the {@link LoginTokenSupplier}.
     */
    public LoginTokenSupplier(String subdomain, String username, char[] password) {
        this.subdomain = subdomain;
        this.username = username;
        this.password = password;
    }

    @Override
    public LoginToken get() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Request.Builder builder = new Request.Builder();
        builder.url("https://" + subdomain + ".qtestnet.com/oauth/token");
        builder.addHeader("Authorization", getSubdomainAuthHeaderValue());
        builder.post(
                new FormBody.Builder()
                        .add("grant_type", "password")
                        .add("username", username)
                        .add("password", String.valueOf(password)).build()
        );

        Request request = builder.build();
        try {
            Response execute = client.newCall(request).execute();
            ObjectMapper objectMapper = new ObjectMapper();
            try (ResponseBody responseBody = execute.body()) {
                String body = requireNonNull(responseBody).string();
                LoginToken loginToken = objectMapper.readValue(body, LoginToken.class);
                return loginToken;
            }
        } catch (IOException e) {
            // No reasonable way to recover from this error, we must pass the buck.
            throw new RuntimeException(e);
        }
    }

    private String getSubdomainAuthHeaderValue() {
        Base64.Encoder encoder = Base64.getEncoder();
        return "Basic " + encoder.encodeToString((subdomain + ":").getBytes());
    }
}
