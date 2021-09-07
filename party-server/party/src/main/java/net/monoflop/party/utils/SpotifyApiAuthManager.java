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
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import org.apache.hc.core5.http.ParseException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Auth manager for spotify api
 *
 * The spotify api implementation has no automatic token refresh mechanism, so
 * we refresh the token manually.
 */
public class SpotifyApiAuthManager {
    private final SpotifyApi spotifyApi;
    private LocalDateTime expireTime;

    public SpotifyApiAuthManager(@Nonnull SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
        this.expireTime = LocalDateTime.now();
    }

    public void refresh() throws IOException, SpotifyWebApiException, ParseException {
        LocalDateTime now = LocalDateTime.now();
        //Refresh token if it is expired
        if(expireTime.isBefore(now) || expireTime.isEqual(now)) {
            internalRefresh();
        }
    }

    public void putToken(@Nonnull String accessToken, int expiresIn) {
        spotifyApi.setAccessToken(accessToken);
        expireTime = LocalDateTime.now().plusSeconds(expiresIn);
    }

    private void internalRefresh() throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        expireTime = LocalDateTime.now().plusSeconds(authorizationCodeCredentials.getExpiresIn());
    }
}
