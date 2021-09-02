/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import javax.validation.constraints.NotEmpty;
import java.util.Dictionary;
import java.util.Map;

public class PartyServerConfiguration extends Configuration {
    @NotEmpty
    private String spotifyClientId;

    @NotEmpty
    private String spotifyClientSecret;

    @NotEmpty
    private String serverBaseUrl;

    @NotEmpty
    private String hashedApiKey;

    @NotNull
    private boolean appendCorsHeaders;

    @Nullable
    private Map<String, String> corsHeaders;

    public String getSpotifyClientId() {
        return spotifyClientId;
    }

    public void setSpotifyClientId(String spotifyClientId) {
        this.spotifyClientId = spotifyClientId;
    }

    public String getSpotifyClientSecret() {
        return spotifyClientSecret;
    }

    public void setSpotifyClientSecret(String spotifyClientSecret) {
        this.spotifyClientSecret = spotifyClientSecret;
    }

    public String getServerBaseUrl() {
        return serverBaseUrl;
    }

    public void setServerBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public String getHashedApiKey() {
        return hashedApiKey;
    }

    public void setHashedApiKey(String hashedApiKey) {
        this.hashedApiKey = hashedApiKey;
    }

    public boolean isAppendCorsHeaders() {
        return appendCorsHeaders;
    }

    public void setAppendCorsHeaders(boolean appendCorsHeaders) {
        this.appendCorsHeaders = appendCorsHeaders;
    }

    @Nullable
    public Map<String, String> getCorsHeaders() {
        return corsHeaders;
    }

    public void setCorsHeaders(@Nullable Map<String, String> corsHeaders) {
        this.corsHeaders = corsHeaders;
    }
}
