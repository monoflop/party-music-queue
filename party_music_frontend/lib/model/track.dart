/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

class Track {
  final String id;
  final String title;
  final String artist;
  final String img;
  final String album;
  final int duration;
  final int progress;

  Track({
    required this.id,
    required this.title,
    required this.artist,
    required this.img,
    required this.album,
    required this.duration,
    required this.progress,
  });

  factory Track.fromJson(Map<String, dynamic> json) {
    return Track(
      id: json["id"]! as String,
      title: json["title"] as String,
      artist: json["artist"] as String,
      img: json["img"] as String,
      album: json["album"] as String,
      duration: json["duration"] as int,
      progress: json["progress"] as int,
    );
  }
}
