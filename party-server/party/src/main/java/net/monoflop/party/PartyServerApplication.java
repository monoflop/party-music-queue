/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.monoflop.party.resources.AuthenticationResource;
import net.monoflop.party.resources.PlaybackResource;
import net.monoflop.party.utils.BaseAuthenticator;
import net.monoflop.party.utils.CorsFilter;
import net.monoflop.party.utils.SpotifyWrapper;
import net.monoflop.party.utils.StateManager;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

public class PartyServerApplication extends Application<PartyServerConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(PartyServerApplication.class);

    public static void main(final String[] args) throws Exception {
        new PartyServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "partyserver";
    }

    @Override
    public void initialize(final Bootstrap<PartyServerConfiguration> bootstrap) {

    }

    @Override
    public void run(final PartyServerConfiguration configuration,
                    final Environment environment) throws IOException {

        //Setup environment
        final String currentDirectory = System.getProperty("user.dir");
        File rootDirectory = new File(currentDirectory);
        File logDirectory = new File(rootDirectory, "data/log");
        if(!logDirectory.exists()) {
            boolean created = logDirectory.mkdirs();
            if(!created) {
                log.error("Failed to create directory: " + logDirectory.getAbsolutePath());
                throw new IOException("Failed to create directory: " + logDirectory.getAbsolutePath());
            }
        }

        log.info("Directory root: " + rootDirectory.getAbsolutePath());
        log.info("Log directory: " + logDirectory.getAbsolutePath());

        //Time config and locale
        log.info("" + LocalDateTime.now() + " - " + LocalDate.now() + " - " + TimeZone.getDefault());
        log.info("Locale: " + Locale.getDefault());

        //CORS
        if(configuration.isAppendCorsHeaders()) {
            if(configuration.getCorsHeaders() == null) {
                log.warn("Cors header are enabled but not provided.");
            }
            else {
                environment.jersey().register(new CorsFilter(configuration.getCorsHeaders()));
            }
        }

        //Auth
        AuthFilter<?, ?> apiAuthenticator = new OAuthCredentialAuthFilter.Builder<>()
                .setAuthenticator(new BaseAuthenticator(configuration.getHashedApiKey()))
                .setPrefix("Key")
                .buildAuthFilter();
        environment.jersey().register(new AuthDynamicFeature(apiAuthenticator));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(PrincipalImpl.class));

        //Read state
        File stateFile = new File(rootDirectory, "data/state.json");
        StateManager stateManager = new StateManager(stateFile);
        environment.lifecycle().manage(stateManager);
        stateManager.load();

        SpotifyWrapper spotifyWrapper = new SpotifyWrapper(stateManager,
                configuration.getSpotifyClientId(),
                configuration.getSpotifyClientSecret(),
                configuration.getServerBaseUrl());

        environment.jersey().register(
                new AuthenticationResource(spotifyWrapper));

        environment.jersey().register(
                new PlaybackResource(spotifyWrapper));
    }
}
