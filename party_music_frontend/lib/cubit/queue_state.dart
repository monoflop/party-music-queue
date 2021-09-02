/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

part of 'queue_cubit.dart';

//TODO improve cubit state design
@immutable
abstract class QueueState {
  const QueueState();
}

class QueueLoading extends QueueState {
  const QueueLoading();
}

class QueueLoaded extends QueueState {
  final Track track;
  const QueueLoaded(this.track);

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is QueueLoaded && o.track == track;
  }

  @override
  int get hashCode => track.hashCode;
}

class QueueAdded extends QueueState {
  const QueueAdded();
}

class QueueError extends QueueState {
  final String message;
  const QueueError(this.message);

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is QueueError && o.message == message;
  }

  @override
  int get hashCode => message.hashCode;
}
