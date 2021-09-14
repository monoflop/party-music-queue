package net.monoflop.party;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.requests.data.player.AddItemToUsersPlaybackQueueRequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import net.monoflop.party.api.Track;
import net.monoflop.party.resources.PlaybackResource;
import net.monoflop.party.utils.SpotifyWrapper;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

public class QueueTelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(QueueTelegramBot.class);
    private static final String COMMAND_START = "/start";
    private static final String COMMAND_SONG = "/song";
    private static final String RESPONSE_ERROR = "Irgendetwas hat leider nicht funktioniert \uD83E\uDD16";
    private static final String RESPONSE_INTRO =
            "Moin moin.\n" +
            "\n" +
            "Schick mir einfach einen Spotify-Link und ich füge den Song der Warteschlange hinzu!\n" +
            "\n" +
            "\uD83D\uDCA1 Sonstige Funktionen:\n" +
                    COMMAND_SONG + " Zeigt dir den aktuellen Song an\n" +
            "\n" +
            "Viel Spaß! \uD83E\uDD73";
    private static final String RESPONSE_NOTHING_PLAYING = "Im Moment wird leider nichts wiedergegeben.";
    private static final String RESPONSE_SONG_NOT_FOUND = "Ich konnte den Song leider nicht finden.";
    //Song name is prefix
    private static final String RESPONSE_SONG_ADDED = " hinzugefügt! \uD83D\uDC83\uD83D\uDD7A\n" +
            "\n" +
            "Es kann ein paar Minuten dauern bis dein Song abgespielt wird.";

    private final String botUsername;
    private final String botToken;
    private final SpotifyWrapper spotifyWrapper;

    public QueueTelegramBot(@Nonnull String botUsername,
                            @Nonnull String botToken,
                            @Nonnull SpotifyWrapper spotifyWrapper) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.spotifyWrapper = spotifyWrapper;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()) {
            log.warn("Telegram server bot received invalid message");
            return;
        }

        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        log.info("Received update in #" + chatId + " message: " + text);

        //Send start message
        if(text.startsWith(COMMAND_START)) {
            postMessage(RESPONSE_INTRO, chatId);
        }
        else if(text.startsWith(COMMAND_SONG)) {
            //Get playing song
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

                if(currentlyPlayingContext != null) {
                    IPlaylistItem iPlaylistItem = currentlyPlayingContext.getItem();
                    com.wrapper.spotify.model_objects.specification.Track spotifyTrack = (com.wrapper.spotify.model_objects.specification.Track) iPlaylistItem;
                    //Construct link, because telegram automatically shows song info
                    postMessage(spotifyTrack.getExternalUrls().getExternalUrls().get("spotify"), chatId);
                    if(!currentlyPlayingContext.getIs_playing()) {
                        postMessage("Wiedergabe pausiert", chatId);
                    }
                }
                else {
                    postMessage(RESPONSE_NOTHING_PLAYING, chatId);
                }
            }
            catch (IOException | SpotifyWebApiException | ParseException e) {
                log.error("Failed to access spotify api", e);
                postMessage(RESPONSE_ERROR, chatId);
            }
        }
        else if(isTextPlayable(text)) {
            //Search song and add to queue
            //Extract trackId
            //Extract spotify id from link or URI
            String spotifyLink = text.replaceAll("https://open.spotify.com/track/", "");
            spotifyLink = spotifyLink.replaceAll("spotify:track:", "");
            int questionMarkIndex = spotifyLink.indexOf("?");
            if (questionMarkIndex != -1) {
                spotifyLink = spotifyLink.substring(0, questionMarkIndex);
            }

            //Check if extracted part is base62
            if (!spotifyLink.matches("^[a-zA-Z0-9]+$")) {
                log.warn("Link invalid");
                postMessage(RESPONSE_SONG_NOT_FOUND, chatId);
                return;
            }

            try {
                Optional<SpotifyApi> spotifyApiOptional = spotifyWrapper.getApi(true);
                if(!spotifyApiOptional.isPresent()) {
                    throw new IllegalStateException("Failed to retrieve spotify api");
                }

                SpotifyApi spotifyApi = spotifyApiOptional.get();

                //Search track
                Optional<Track> targetTrack = PlaybackResource.searchTrack(spotifyApi, spotifyLink);
                if(!targetTrack.isPresent()) {
                    log.warn("No track found with id");
                    postMessage(RESPONSE_SONG_NOT_FOUND, chatId);
                    return;
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
                    postMessage(RESPONSE_NOTHING_PLAYING, chatId);
                    return;
                }

                //Queue track
                AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = spotifyApi
                        .addItemToUsersPlaybackQueue("spotify:track:" + spotifyLink)
                        .build();
                addItemToUsersPlaybackQueueRequest.execute();
                postMessage("\"" + targetTrack.get().getTitle() + "\" " + RESPONSE_SONG_ADDED, chatId);
            }
            catch (IOException | SpotifyWebApiException | ParseException e) {
                log.error("Failed to access spotify api", e);
                postMessage(RESPONSE_ERROR, chatId);
            }
        }
    }

    private boolean isTextPlayable(@Nonnull String text) {
        //Check if text is a spotify link or track id
        return text.startsWith("https://open.spotify.com/track/")
                || text.startsWith("spotify:track:");
    }

    private void postMessage(@Nonnull String messageString, long chatId) {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(String.valueOf(chatId));
        message.setText(messageString);
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram message", e);
        }
    }
}
