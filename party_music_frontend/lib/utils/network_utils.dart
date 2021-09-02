/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:party_music_frontend/model/api.dart';

//TODO improve
class NetworkUtils {
  static Api? apiCache;

  static Future<Api> getApiConfig() async {
    if (apiCache == null) {
      String apiJsonString = await rootBundle.loadString('assets/api.json');
      dynamic apiJson = jsonDecode(apiJsonString)!;
      apiCache = Api.fromJson(apiJson);
    }
    return apiCache!;
  }
}
