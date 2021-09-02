/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:flutter/material.dart';
import 'package:party_music_frontend/ui/ui_queue.dart';
import 'package:party_music_frontend/utils/ui_utils.dart';

const desktopInputButtonFont = 20.0;
const desktopInputEdge = 30.0;
const desktopIconSize = 32.0;
const desktopButtonHeight = 80.0;

const mobileInputButtonFont = 14.0;
const mobileInputEdge = 20.0;
const mobileIconSize = 24.0;
const mobileButtonHeight = 50.0;

class InputUi extends StatelessWidget {
  final TextEditingController _firstNameController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Column(mainAxisSize: MainAxisSize.max, children: <Widget>[
      TextField(
        controller: _firstNameController,
        textAlign: TextAlign.center,
        style: TextStyle(
          fontSize: UiUtils.isMobile(context)
              ? mobileInputButtonFont
              : desktopInputButtonFont,
        ),
        decoration: InputDecoration(
            contentPadding: EdgeInsets.symmetric(
                vertical: UiUtils.isMobile(context)
                    ? mobileInputEdge
                    : desktopInputEdge,
                horizontal: (UiUtils.isMobile(context)
                        ? mobileInputEdge
                        : desktopInputEdge) /
                    2.0),
            border: OutlineInputBorder(borderRadius: BorderRadius.zero),
            hintText: "Spotify-Link",
            prefixIcon: Padding(
                padding: EdgeInsets.only(
                    left: (UiUtils.isMobile(context)
                            ? mobileInputEdge
                            : desktopInputEdge) /
                        2.0),
                child: Icon(Icons.link,
                    size: UiUtils.isMobile(context)
                        ? mobileIconSize
                        : desktopIconSize)),
            suffixIcon: Padding(
              padding: EdgeInsets.only(
                  right: (UiUtils.isMobile(context)
                          ? mobileInputEdge
                          : desktopInputEdge) /
                      2.0),
              child: InkWell(
                  child: Icon(Icons.clear,
                      size: UiUtils.isMobile(context)
                          ? mobileIconSize
                          : desktopIconSize),
                  onTap: () {
                    _firstNameController.clear();
                  }),
            )),
      ),
      ElevatedButton(
        style: ElevatedButton.styleFrom(
            //textStyle: const TextStyle(fontSize: 20),
            minimumSize: Size(
                double.infinity,
                UiUtils.isMobile(context)
                    ? mobileButtonHeight
                    : desktopButtonHeight),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.zero)),
        onPressed: () {
          showDialog<String>(
              context: context,
              builder: (BuildContext context) =>
                  QueueDialog(_firstNameController.text));
        },
        child: Text(
          "Song hinzufügen",
          style: TextStyle(
              fontSize: UiUtils.isMobile(context)
                  ? mobileInputButtonFont
                  : desktopInputButtonFont,
              fontWeight: FontWeight.bold),
        ),
      ),
    ]);
  }
}
