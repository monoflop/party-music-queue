/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:party_music_frontend/cubit/playback_cubit.dart';
import 'package:party_music_frontend/ui/ui_input.dart';
import 'package:party_music_frontend/ui/ui_playback.dart';
import 'package:party_music_frontend/ui/ui_tutorial.dart';
import 'package:party_music_frontend/utils/services/playback_repository.dart';

void main() {
  runApp(MainApp());
}

/// Without this wrapper Bloc is injected into wrong context
class MainApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "Musikwünsche",
      theme: ThemeData(primarySwatch: Colors.blue, brightness: Brightness.dark),
      home: BlocProvider(
          create: (context) => PlaybackCubit(PlaybackRepositoryImpl()),
          child: MainPage()),
    );
  }
}

class MainPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    AppBar appBar = AppBar(
      title: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.audiotrack,
            color: Colors.white,
            size: 30.0,
          ),
          Container(
              padding: const EdgeInsets.all(8.0),
              child:
                  Text("MUSIKWÜNSCHE", style: GoogleFonts.openSansCondensed()))
        ],
      ),
      backgroundColor: Colors.black,
      elevation: 0.0,
      centerTitle: true,
      automaticallyImplyLeading: false,
      leading: IconButton(
        icon: Icon(Icons.refresh),
        onPressed: () {
          BlocProvider.of<PlaybackCubit>(context).getPlayback();
        },
      ),
    );
    return Scaffold(
        primary: true,
        appBar: appBar,
        body: Column(
          mainAxisSize: MainAxisSize.max,
          children: <Widget>[
            PlaybackUi(),
            TutorialUi(),
            InputUi(),
          ],
        ));
  }
}

/*
return Scaffold(
        primary: true,
        appBar: appBar,
        body: Column(
          mainAxisSize: MainAxisSize.max,
          children: <Widget>[
            PlaybackUi(),
            TutorialUi(),
            InputUi(),
          ],
        ));
 */

/*return Scaffold(
        resizeToAvoidBottomInset: true,
        primary: true,
        appBar: appBar,
        body: SingleChildScrollView(
          child: SizedBox(
            height: MediaQuery.of(context).size.height -
                (appBar.preferredSize.height +
                    MediaQuery.of(context).padding.top +
                    MediaQuery.of(context).padding.bottom),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              children: <Widget>[
                PlaybackUi(),
                TutorialUi(),
                InputUi(),
              ],
            ),
          ),
        ));*/
