/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

package net.monoflop.party.api;

public class Playback {
    private boolean playing;
    private Track active;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Track getActive() {
        return active;
    }

    public void setActive(Track active) {
        this.active = active;
    }
}
