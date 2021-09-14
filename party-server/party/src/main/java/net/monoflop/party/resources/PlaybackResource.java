/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.resources;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.requests.data.player.AddItemToUsersPlaybackQueueRequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.PrincipalImpl;
import net.monoflop.party.utils.SpotifyWrapper;
import net.monoflop.party.api.Playback;
import net.monoflop.party.api.RequestPlayForm;
import net.monoflop.party.api.Track;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Optional;

@Path("/playback")
public class PlaybackResource {
    private static final Logger log = LoggerFactory.getLogger(PlaybackResource.class);
    private static final int DESIRED_IMAGE_DIM = 200;
    private final SpotifyWrapper spotifyWrapper;

    public PlaybackResource(@Nonnull SpotifyWrapper spotifyWrapper) {
        this.spotifyWrapper = spotifyWrapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlayback(@Auth PrincipalImpl principal) {
        try {
            Optional<SpotifyApi> spotifyApiOptional = spotifyWrapper.getApi(true);
            if(!spotifyApiOptional.isPresent()) {
                throw new IllegalStateException("Failed to retrieve spotify api");
            }

            SpotifyApi spotifyApi = spotifyApiOptional.get();
            GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = spotifyApi
                    .getInformationAboutUsersCurrentPlayback()
                    .additionalTypes("track")
                    .market(CountryCode.DE)
                    .build();
            CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();

            Playback playback = new Playback();
            playback.setPlaying(false);
            if(currentlyPlayingContext != null) {
                IPlaylistItem iPlaylistItem = currentlyPlayingContext.getItem();
                com.wrapper.spotify.model_objects.specification.Track spotifyTrack = (com.wrapper.spotify.model_objects.specification.Track) iPlaylistItem;

                Track track = PlaybackResource.mapSpotifyTrack(spotifyTrack);
                track.setProgress(currentlyPlayingContext.getProgress_ms());
                playback.setActive(track);
                playback.setPlaying(currentlyPlayingContext.getIs_playing());
            }

            return Response.ok(playback).build();
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Failed to access spotify api", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchSong(@Auth PrincipalImpl principal,
                               @NotNull @Valid RequestPlayForm requestPlayForm) {
        try {
            Optional<SpotifyApi> spotifyApiOptional = spotifyWrapper.getApi(true);
            if(!spotifyApiOptional.isPresent()) {
                throw new IllegalStateException("Failed to retrieve spotify api");
            }

            SpotifyApi spotifyApi = spotifyApiOptional.get();

            //Search track
            Optional<Track> targetTrack = PlaybackResource.searchTrack(spotifyApi, requestPlayForm.getTrackId());
            if(!targetTrack.isPresent()) {
                log.warn("No track found with id");
                return Response.status(400).build();
            }
            return Response.ok(targetTrack.get()).build();
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Failed to access spotify api", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response playSong(@Auth PrincipalImpl principal,
                             @NotNull @Valid RequestPlayForm requestPlayForm) {
        try {
            Optional<SpotifyApi> spotifyApiOptional = spotifyWrapper.getApi(true);
            if(!spotifyApiOptional.isPresent()) {
                throw new IllegalStateException("Failed to retrieve spotify api");
            }

            SpotifyApi spotifyApi = spotifyApiOptional.get();

            //Search track
            Optional<Track> targetTrack = PlaybackResource.searchTrack(spotifyApi, requestPlayForm.getTrackId());
            if(!targetTrack.isPresent()) {
                log.warn("No track found with id");
                return Response.status(400).build();
            }

            //Check if a device is active
            GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = spotifyApi
                    .getInformationAboutUsersCurrentPlayback()
                    .additionalTypes("track")
                    .market(CountryCode.DE)
                    .build();
            CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
            if(currentlyPlayingContext == null) {
                log.warn("No song is playing at the moment");
                return Response.status(423).build();
            }

            //Queue track
            AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = spotifyApi
                    .addItemToUsersPlaybackQueue("spotify:track:" + requestPlayForm.getTrackId())
                    .build();
            addItemToUsersPlaybackQueueRequest.execute();

            //Return track
            return Response.ok(targetTrack.get()).build();
        }
        catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Failed to access spotify api", e);
            return Response.serverError().build();
        }
    }

    @Nonnull
    public static Optional<Track> searchTrack(
            @Nonnull SpotifyApi spotifyApi, @Nonnull String trackId)
            throws IOException, SpotifyWebApiException, ParseException {
        //Search track first
        try {
            GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId)
                    .build();
            com.wrapper.spotify.model_objects.specification.Track spotifyTrack = getTrackRequest.execute();
            if(spotifyTrack == null) {
                return Optional.empty();
            }

            //Return track
            Track track = PlaybackResource.mapSpotifyTrack(spotifyTrack);
            return Optional.of(track);
        }
        catch (BadRequestException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    public static Track mapSpotifyTrack(@Nonnull com.wrapper.spotify.model_objects.specification.Track spotifyTrack) {
        Track track = new Track();
        track.setId(spotifyTrack.getId());
        track.setTitle(spotifyTrack.getName());
        track.setDuration(spotifyTrack.getDurationMs());
        StringBuilder artistStringBuilder = new StringBuilder();
        for(ArtistSimplified artistSimplified : spotifyTrack.getAlbum().getArtists()) {
            artistStringBuilder.append(artistSimplified.getName()).append(" ");
        }
        track.setArtist(artistStringBuilder.toString().trim());
        track.setAlbum(spotifyTrack.getAlbum().getName());

        Image[] images = spotifyTrack.getAlbum().getImages();
        if(images.length > 0) {
            Image selectedImage = null;
            for(Image image : images) {
                if(selectedImage == null || Math.abs(DESIRED_IMAGE_DIM - image.getWidth())
                        < Math.abs(DESIRED_IMAGE_DIM - selectedImage.getWidth())) {
                    selectedImage = image;
                }
            }

            track.setImg(selectedImage.getUrl());
        }
        return track;
    }
}
