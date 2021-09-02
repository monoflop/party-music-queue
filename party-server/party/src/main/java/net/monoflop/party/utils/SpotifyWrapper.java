/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.utils;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import org.apache.hc.core5.http.ParseException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * Wrapper for spotify api and user authentication process
 */
public class SpotifyWrapper {
    private static final String SPOTIFY_REFRESH_TOKEN_KEY = "spotify_refresh_token";

    private final SpotifyApiAuthManager spotifyApiAuthManager;
    private final SpotifyApi spotifyApi;
    private final StateManager stateManager;
    private boolean authenticated;

    public SpotifyWrapper(@Nonnull StateManager stateManager,
                          @Nonnull String clientId,
                          @Nonnull String clientSecret,
                          @Nonnull String serverBaseUrl) {
        this.stateManager = stateManager;
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(URI.create(serverBaseUrl + "/v1/auth/spotify/callback/"))
                .build();
        this.spotifyApiAuthManager = new SpotifyApiAuthManager(spotifyApi);

        //Check if an account is linked
        String refreshToken = stateManager.getString(SPOTIFY_REFRESH_TOKEN_KEY, null);
        if(refreshToken == null) {
            this.authenticated = false;
        }
        else {
            this.spotifyApi.setRefreshToken(refreshToken);
            this.authenticated = true;
        }
    }

    public Optional<SpotifyApi> getApi(boolean requireAuth) throws IOException, SpotifyWebApiException, ParseException {
        //Return authenticated api for user access
        if(requireAuth) {
            //Check if api is authenticated
            if(!this.authenticated) {
                return Optional.empty();
            }

            //Refresh token and return api
            spotifyApiAuthManager.refresh();
        }
        return Optional.of(spotifyApi);
    }

    public void clearAuthentication() {
        authenticated = false;
        stateManager.setString(SPOTIFY_REFRESH_TOKEN_KEY, null);
        spotifyApi.setRefreshToken(null);
        spotifyApi.setAccessToken(null);
    }

    public void authenticate(@Nonnull String code) throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        spotifyApiAuthManager.putToken(authorizationCodeCredentials.getAccessToken(), authorizationCodeCredentials.getExpiresIn());
        stateManager.setString(SPOTIFY_REFRESH_TOKEN_KEY, authorizationCodeCredentials.getRefreshToken());
    }
}
