/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:flutter/material.dart';
import 'package:party_music_frontend/utils/ui_utils.dart';

//TODO improve responsive design
const desktopTitleSize = 35.0;
const desktopBulletSize = 30.0;
const desktopBulletFontSize = 20.0;

const mobileTitleSize = 20.0;
const mobileBulletSize = 16.0;
const mobileBulletFontSize = 12.0;

class TutorialUi extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Theme(
      data: ThemeData(
        splashColor: Colors.transparent,
        highlightColor: Colors.transparent,
      ),
      child: Expanded(
        child: ListView(
          shrinkWrap: true,
          children: [
            SizedBox(height: 40),
            Center(
              child: Text("So geht's:",
                  style: TextStyle(
                      fontSize: UiUtils.isMobile(context)
                          ? mobileTitleSize
                          : desktopTitleSize,
                      fontWeight: FontWeight.bold)),
            ),
            SizedBox(height: 20),
            Center(
                child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                BulletElement("Song bei Spotify suchen"),
                BulletElement("Song-Link teilen"),
                BulletElement("Link hier hinzufügen"),
                BulletElement("..."),
                BulletElement("Profit"),
              ],
            )),
          ],
        ),
      ),
    );
  }
}

class BulletElement extends StatelessWidget {
  final String text;

  BulletElement(this.text);

  @override
  Widget build(BuildContext context) {
    return Padding(
        padding: EdgeInsets.all(10),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Icon(
              Icons.control_point,
              color: Colors.blue,
              size: UiUtils.isMobile(context)
                  ? mobileBulletSize
                  : desktopBulletSize,
            ),
            SizedBox(width: 20),
            Text(text,
                style: TextStyle(
                    fontSize: UiUtils.isMobile(context)
                        ? mobileBulletFontSize
                        : desktopBulletFontSize))
          ],
        ));
  }
}
