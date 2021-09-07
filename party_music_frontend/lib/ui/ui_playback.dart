/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:party_music_frontend/cubit/playback_cubit.dart';
import 'package:party_music_frontend/model/playback.dart';
import 'package:party_music_frontend/utils/ui_utils.dart';
import 'package:shimmer/shimmer.dart';

const shimmerBackground = Color.fromARGB(255, 41, 41, 41);
const shimmerForeground = Color.fromARGB(255, 84, 84, 84);

//TODO improve responsive design
const desktopPlaybackSize = 400.0;
const desktopImageSize = 100.0;
const desktopSongTitleSize = 50.0;
const desktopSongArtistSize = 20.0;
const desktopSongTitlePlaceholderWidth = 400.0;
const desktopSongTitlePlaceholderHeight = 60.0;
const desktopSongArtistPlaceholderWidth = 200.0;
const desktopSongArtistPlaceholderHeight = 40.0;

const mobilePlaybackSize = 250.0;
const mobileImageSize = 50.0;
const mobileSongTitleSize = 25.0;
const mobileSongArtistSize = 15.0;
const mobileSongTitlePlaceholderWidth = 200.0;
const mobileSongTitlePlaceholderHeight = 30.0;
const mobileSongArtistPlaceholderWidth = 100.0;
const mobileSongArtistPlaceholderHeight = 20.0;

class PlaybackUi extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: UiUtils.isMobile(context)
          ? mobilePlaybackSize //MediaQuery.of(context).size.height * 0.3
          : desktopPlaybackSize,
      color: Colors.transparent,
      child: new Container(
        decoration: new BoxDecoration(
            color: Colors.black,
            borderRadius: new BorderRadius.only(
              bottomLeft: const Radius.circular(80.0),
              bottomRight: const Radius.circular(80.0),
            )),
        child: BlocConsumer<PlaybackCubit, PlaybackState>(
          listener: (context, state) {
            if (state is PlaybackError) {
              Scaffold.of(context)
                  .showSnackBar(SnackBar(content: Text(state.message)));
            }
          },
          builder: (context, state) {
            print(state);
            if (state is PlaybackLoaded) {
              return LoadedPlaybackView(state.playback);
            } else {
              return LoadingPlaybackView();
            }
          },
        ),
      ),
    );
  }
}

class LoadingPlaybackView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Shimmer.fromColors(
        baseColor: shimmerBackground,
        highlightColor: shimmerForeground,
        child: new Center(
            child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircleAvatar(
              radius: UiUtils.isMobile(context)
                  ? mobileImageSize
                  : desktopImageSize,
              backgroundColor: Colors.black,
              child: Icon(
                Icons.audiotrack,
                color: Colors.white,
                size: 30.0,
              ),
            ),
            SizedBox(height: 40),
            Container(
              width: UiUtils.isMobile(context)
                  ? mobileSongTitlePlaceholderWidth
                  : desktopSongTitlePlaceholderWidth,
              height: UiUtils.isMobile(context)
                  ? mobileSongTitlePlaceholderHeight
                  : desktopSongTitlePlaceholderHeight,
              decoration: BoxDecoration(
                color: Colors.black,
              ),
            ),
            SizedBox(height: 20),
            Container(
              width: UiUtils.isMobile(context)
                  ? mobileSongArtistPlaceholderWidth
                  : desktopSongArtistPlaceholderWidth,
              height: UiUtils.isMobile(context)
                  ? mobileSongArtistPlaceholderHeight
                  : desktopSongArtistPlaceholderHeight,
              decoration: BoxDecoration(
                color: Colors.black,
              ),
            ),
          ],
        )));
  }
}

class LoadedPlaybackView extends StatelessWidget {
  final Playback playback;

  LoadedPlaybackView(this.playback);

  @override
  Widget build(BuildContext context) {
    Widget image;
    String title;
    String artist;
    if (playback.playing && playback.active != null) {
      image = CircleAvatar(
        radius: UiUtils.isMobile(context) ? mobileImageSize : desktopImageSize,
        backgroundImage: NetworkImage(playback.active!.img),
      );
      title = playback.active!.title;
      artist = playback.active!.artist;
    } else {
      image = CircleAvatar(
        radius: UiUtils.isMobile(context) ? mobileImageSize : desktopImageSize,
        backgroundColor: Color.fromARGB(255, 41, 41, 41),
        child: Icon(
          Icons.audiotrack,
          color: Colors.white,
          size: 30.0,
        ),
      );
      title = "Pausiert";
      artist = "Aktuell wird nichts wiedergegeben";
    }

    return new Center(
        child: Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        image,
        SizedBox(height: 40),
        Text(title,
            maxLines: 1,
            textAlign: TextAlign.center,
            overflow: TextOverflow.ellipsis,
            style: TextStyle(
                fontSize: UiUtils.isMobile(context)
                    ? mobileSongTitleSize
                    : desktopSongTitleSize,
                fontWeight: FontWeight.bold)),
        SizedBox(height: 20),
        Text(
          artist,
          maxLines: 1,
          textAlign: TextAlign.center,
          overflow: TextOverflow.ellipsis,
          style: TextStyle(
              color: Colors.grey,
              fontSize: UiUtils.isMobile(context)
                  ? mobileSongArtistSize
                  : desktopSongArtistSize),
        )
      ],
    ));
  }
}
