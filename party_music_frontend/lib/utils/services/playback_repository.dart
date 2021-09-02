/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

import 'dart:convert';

import 'package:http/http.dart';
import 'package:party_music_frontend/constants/network_constants.dart';
import 'package:party_music_frontend/model/api.dart';
import 'package:party_music_frontend/model/playback.dart';
import 'package:party_music_frontend/utils/network_utils.dart';

import '../network_exception.dart';

abstract class PlaybackRepository {
  Future<Playback> getPlayback();
}

//TODO pass api config as parameters
class PlaybackRepositoryImpl implements PlaybackRepository {
  @override
  Future<Playback> getPlayback() async {
    Api apiConfig = await NetworkUtils.getApiConfig();
    Response response =
        await get(Uri.parse(apiConfig.url + "/playback"), headers: {
      "Authorization": "Key " + apiConfig.key,
      //"Access-Control-Allow-Origin": "*",
      //"Access-Control-Allow-Methods": "GET",
    });
    if (response.statusCode == httpOk) {
      dynamic body = jsonDecode(Utf8Decoder().convert(response.bodyBytes))!;
      Playback playback = Playback.fromJson(body);
      return playback;
    } else {
      throw NetworkException();
    }
  }
}
