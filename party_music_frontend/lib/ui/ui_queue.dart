/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:party_music_frontend/cubit/queue_cubit.dart';
import 'package:party_music_frontend/model/track.dart';
import 'package:party_music_frontend/utils/services/queue_repository.dart';

const double dialogButtonWidth = 100;
const double dialogButtonHeight = 40;

class QueueDialog extends StatelessWidget {
  final String _link;

  QueueDialog(this._link);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
        create: (context) => QueueCubit(QueueRepositoryImpl(), _link),
        child: BlocConsumer<QueueCubit, QueueState>(
          listener: (context, state) {
            if (state is QueueError) {
              Scaffold.of(context)
                  .showSnackBar(SnackBar(content: Text(state.message)));
            }
          },
          builder: (context, state) {
            print(state);
            if (state is QueueLoading) {
              return QueueLoadingDialog();
            } else if (state is QueueError) {
              return QueueErrorDialog(state.message);
            } else if (state is QueueLoaded) {
              return QueueLoadedDialog(state.track);
            } else if (state is QueueAdded) {
              return QueueAddedDialog();
            }
            return QueueLoadingDialog();
          },
        ));
  }
}

class QueueLoadedDialog extends StatelessWidget {
  final Track _track;

  QueueLoadedDialog(this._track);

  @override
  Widget build(BuildContext context) {
    Duration songDuration = Duration(milliseconds: _track.duration);
    return AlertDialog(
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(20))),
      title: const Text("Song hinzufügen?"),
      content: Row(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Image(width: 64, height: 64, image: NetworkImage(_track.img)),
          SizedBox(
            width: 20,
          ),
          Column(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(_track.title,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(fontWeight: FontWeight.bold)),
              Text(_track.artist,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(color: Colors.grey)),
              Text(
                  "${songDuration.inMinutes.remainder(60)}:${(songDuration.inSeconds.remainder(60))}",
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(color: Colors.grey)),
            ],
          )
        ],
      ),
      actions: <Widget>[
        SizedBox(
          width: dialogButtonWidth,
          height: dialogButtonHeight,
          child: TextButton(
            onPressed: () {
              BlocProvider.of<QueueCubit>(context).querySong(_track.id);
            },
            child: const Text("Hinzufügen"),
          ),
        ),
        SizedBox(
          width: dialogButtonWidth,
          height: dialogButtonHeight,
          child: TextButton(
            onPressed: () => Navigator.pop(context, 'Cancel'),
            child: const Text("Abbrechen"),
          ),
        ),
      ],
    );
  }
}

class QueueLoadingDialog extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(20))),
      title: const Text("Lade"),
      content: const Text("Song wird verarbeitet..."),
      actions: <Widget>[
        SizedBox(
          width: dialogButtonWidth,
          height: dialogButtonHeight,
          child: TextButton(
            onPressed: () => Navigator.pop(context, 'Cancel'),
            child: const Text("Abbrechen"),
          ),
        ),
      ],
    );
  }
}

class QueueErrorDialog extends StatelessWidget {
  final String _message;

  QueueErrorDialog(this._message);

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(20))),
      title: const Text("Fehler"),
      content: Text(_message),
      actions: <Widget>[
        SizedBox(
          width: dialogButtonWidth,
          height: dialogButtonHeight,
          child: TextButton(
            onPressed: () => Navigator.pop(context, 'Cancel'),
            child: const Text("Ok"),
          ),
        ),
      ],
    );
  }
}

class QueueAddedDialog extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(20))),
      title: const Text("Song hinzugefügt!"),
      content: Row(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(
          Icons.check_circle_outline,
          color: Colors.blue,
          size: 80,
        ),
      ]),
      actions: <Widget>[
        SizedBox(
          width: dialogButtonWidth,
          height: dialogButtonHeight,
          child: TextButton(
            onPressed: () => Navigator.pop(context, 'Cancel'),
            child: const Text("Ok"),
          ),
        ),
      ],
    );
  }
}
