/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.resources;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import net.monoflop.party.utils.SpotifyWrapper;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

//TODO Add authentication interface or something
@Path("/auth/spotify")
public class AuthenticationResource {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationResource.class);
    private final SpotifyWrapper spotifyWrapper;

    public AuthenticationResource(@Nonnull SpotifyWrapper spotifyWrapper) {
        this.spotifyWrapper = spotifyWrapper;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getAuthorizationPage() {
        try {
            Optional<SpotifyApi> spotifyApiOptional = spotifyWrapper.getApi(false);
            if(!spotifyApiOptional.isPresent()) {
                throw new IllegalStateException("Failed to retrieve spotify api");
            }

            SpotifyApi spotifyApi = spotifyApiOptional.get();
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope("user-read-playback-state,user-modify-playback-state")
                    .build();
            final URI uri = authorizationCodeUriRequest.execute();
            return Response.temporaryRedirect(uri).build();
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Failed to access spotify api", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_HTML)
    public Response logout() {
        spotifyWrapper.clearAuthentication();
        return Response.ok().build();
    }

    @GET
    @Path("/callback")
    @Produces(MediaType.TEXT_HTML)
    public Response getCallbackResult(@Nullable @QueryParam("code") String code,
                                      @Nullable @QueryParam("error") String error) {
        if(error != null || code == null) {
            log.warn("Authentication failed with error: " + error);
            return Response.ok().build();
        }
        log.info("Authentication with code " + code);

        try {
            spotifyWrapper.authenticate(code);
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Failed to authenticate spotify api", e);
            return Response.ok().build();
        }

        return Response.ok().build();
    }
}
