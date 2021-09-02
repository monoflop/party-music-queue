/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'package:flutter/material.dart';

const kTabletBreakpoint = 768.0;

class UiUtils {
  static bool isMobile(BuildContext context) {
    return MediaQuery.of(context).size.width < kTabletBreakpoint;
  }
}
