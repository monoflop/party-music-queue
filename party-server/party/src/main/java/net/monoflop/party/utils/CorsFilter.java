/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.utils;

import javax.annotation.Nonnull;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class CorsFilter implements ContainerResponseFilter {
    private final Map<String, String> headers;

    public CorsFilter(@Nonnull Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        headers.forEach((key, value) -> responseContext.getHeaders().add(key, value));
        /*responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "https://monoflop.net");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Access-Control-Allow-Origin, Access-Control-Allow-Methods, Authorization");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");*/
    }
}
