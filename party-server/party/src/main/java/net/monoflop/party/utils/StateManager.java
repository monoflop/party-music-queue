/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.dropwizard.lifecycle.Managed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StateManager implements Managed {
    private final File stateFile;
    private final Gson gson;
    private JsonObject content;

    public StateManager(@Nonnull File stateFile){
        this.stateFile = stateFile;
        this.gson = new Gson();
    }

    public void load() throws IOException {
        if(!stateFile.exists()) {
            saveDefault();
        }
        String fileContent = new String(Files.readAllBytes(Paths.get(stateFile.toURI())), StandardCharsets.UTF_8);
        content = gson.fromJson(fileContent, JsonObject.class);
    }

    public void save() throws IOException {
        String fileContent = gson.toJson(content);
        Files.write(Paths.get(stateFile.toURI()), fileContent.getBytes(StandardCharsets.UTF_8));
    }

    public void saveDefault() throws IOException {
        String fileContent = "{}";
        Files.write(Paths.get(stateFile.toURI()), fileContent.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    public String getString(@Nonnull String key, @Nullable String defaultValue) {
        if(content != null && content.has(key)) {
            return content.get(key).getAsString();
        }
        return defaultValue;
    }

    public void setString(@Nonnull String key, @Nullable String value) {
        if(content != null) {
            content.addProperty(key, value);
        }
    }

    //Dropwizard lifecycle
    @Override
    public void start() {}

    @Override
    public void stop() throws Exception {
        save();
    }
}
