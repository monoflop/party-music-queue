/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PrincipalImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.Principal;
import java.util.Optional;

public class BaseAuthenticator implements Authenticator<String, Principal> {
    private final String key;

    public BaseAuthenticator(@Nonnull String key) {
        this.key = key;
    }

    @Override
    public Optional<Principal> authenticate(@Nullable String authValue) {
        if(authValue == null) {
            return Optional.empty();
        }

        String hashedApiKey = Hashing.sha256().hashString(authValue, Charsets.UTF_8).toString();
        if(hashedApiKey.equals(key)) {
            return Optional.of(new PrincipalImpl("Default"));
        }

        return Optional.empty();
    }
}
