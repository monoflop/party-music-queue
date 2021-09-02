/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:party_music_frontend/model/track.dart';

class Playback {
  final bool playing;
  final Track? active;

  Playback({required this.playing, required this.active});

  factory Playback.fromJson(Map<String, dynamic> json) {
    return Playback(
        playing: json["playing"]! as bool,
        active: json["active"] == null ? null : Track.fromJson(json["active"]));
  }
}
