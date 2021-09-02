/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:bloc/bloc.dart';
import 'package:flutter/material.dart';
import 'package:party_music_frontend/model/track.dart';
import 'package:party_music_frontend/utils/network_exception.dart';
import 'package:party_music_frontend/utils/services/queue_repository.dart';

part 'queue_state.dart';

class QueueCubit extends Cubit<QueueState> {
  final _base62 = RegExp(r'^[a-zA-Z0-9]+$');
  final QueueRepository _queueRepository;
  final String _link;

  QueueCubit(this._queueRepository, this._link) : super(QueueLoading()) {
    searchSong(_link);
  }

  void searchSong(String spotifyLink) async {
    //Validate link
    if (spotifyLink.isEmpty) {
      emit(QueueError("Link darf nicht leer sein."));
      return;
    }
    if (!spotifyLink.startsWith("https://open.spotify.com/track/") &&
        !spotifyLink.startsWith("spotify:track:")) {
      emit(QueueError("Link ungültig."));
      return;
    }

    //Extract spotify id from link or URI
    spotifyLink = spotifyLink.replaceAll("https://open.spotify.com/track/", "");
    spotifyLink = spotifyLink.replaceAll("spotify:track:", "");
    int questionMarkIndex = spotifyLink.indexOf("?");
    if (questionMarkIndex != -1) {
      spotifyLink = spotifyLink.substring(0, questionMarkIndex);
    }

    //Check if extracted part is base62
    if (!_base62.hasMatch(spotifyLink)) {
      emit(QueueError("Link ungültig."));
      return;
    }

    print("Extracted spotify id");
    print(spotifyLink);

    //Search song
    try {
      final Track? track = await _queueRepository.searchTrack(spotifyLink);
      if (track == null) {
        emit(QueueError("Song nicht gefunden."));
        return;
      }
      emit(QueueLoaded(track));
    } on NetworkException catch (e) {
      print(e);
      emit(QueueError("Netzwerk Fehler :("));
    }
  }

  void querySong(String trackId) async {
    try {
      final Track? track = await _queueRepository.queueTrack(trackId);
      if (track == null) {
        emit(QueueError("Wiedergabe ist pausiert, versuchs später nochmal."));
        return;
      }
      emit(QueueAdded());
    } on NetworkException catch (e) {
      print(e);
      emit(QueueError("Netzwerk Fehler :("));
    }
  }
}
