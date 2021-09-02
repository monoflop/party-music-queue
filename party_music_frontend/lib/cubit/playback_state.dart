/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

part of 'playback_cubit.dart';

//TODO improve cubit state design
@immutable
abstract class PlaybackState {
  const PlaybackState();
}

class PlaybackLoading extends PlaybackState {
  const PlaybackLoading();
}

class PlaybackLoaded extends PlaybackState {
  final Playback playback;
  const PlaybackLoaded(this.playback);

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is PlaybackLoaded && o.playback == playback;
  }

  @override
  int get hashCode => playback.hashCode;
}

class PlaybackError extends PlaybackState {
  final String message;
  const PlaybackError(this.message);

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is PlaybackError && o.message == message;
  }

  @override
  int get hashCode => message.hashCode;
}
