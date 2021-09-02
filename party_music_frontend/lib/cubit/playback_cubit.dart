/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:flutter/material.dart';
import 'package:party_music_frontend/model/playback.dart';
import 'package:party_music_frontend/utils/network_exception.dart';
import 'package:party_music_frontend/utils/services/playback_repository.dart';

part 'playback_state.dart';

class PlaybackCubit extends Cubit<PlaybackState> {
  final PlaybackRepository _playbackRepository;

  Timer? timer;

  PlaybackCubit(this._playbackRepository) : super(PlaybackLoading()) {
    getPlayback();
  }

  void getPlayback() async {
    try {
      final Playback playback = await _playbackRepository.getPlayback();

      if (playback.playing && playback.active != null) {
        if (timer != null) {
          timer!.cancel();
        }
        //TODO scheduled multiple times
        int duration =
            playback.active!.duration - playback.active!.progress + 1000;
        print("Scheduled for ${duration} milliseconds");
        if (duration > 0) {
          timer = Timer(Duration(milliseconds: duration), () {
            print("Refreshing");
            timer!.cancel();
            getPlayback();
          });
        }
      }
      emit(PlaybackLoaded(playback));
    } on NetworkException {
      emit(PlaybackError("Network error"));
    }
  }
}
