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
import 'package:party_music_frontend/model/track.dart';
import 'package:party_music_frontend/utils/network_utils.dart';

import '../network_exception.dart';

abstract class QueueRepository {
  Future<Track?> searchTrack(String trackId);
  Future<Track?> queueTrack(String trackId);
}

//TODO pass api config as parameters
class QueueRepositoryImpl implements QueueRepository {
  @override
  Future<Track?> searchTrack(String trackId) async {
    Api apiConfig = await NetworkUtils.getApiConfig();
    Response response =
        await post(Uri.parse(apiConfig.url + "/playback/search"),
            headers: {
              "Authorization": "Key " + apiConfig.key,
              //"Access-Control-Allow-Origin": "*",
              //"Access-Control-Allow-Methods": "POST",
              "Content-Type": "application/json"
            },
            body: "{\"trackId\":\"" + trackId + "\"}");
    if (response.statusCode == httpOk) {
      dynamic body = jsonDecode(Utf8Decoder().convert(response.bodyBytes))!;
      Track track = Track.fromJson(body);
      return track;
    } else if (response.statusCode == httpBadRequest) {
      //Song not found
      return null;
    } else {
      throw NetworkException();
    }
  }

  @override
  Future<Track?> queueTrack(String trackId) async {
    Api apiConfig = await NetworkUtils.getApiConfig();
    Response response = await post(Uri.parse(apiConfig.url + "/playback"),
        headers: {
          "Authorization": "Key " + apiConfig.key,
          //"Access-Control-Allow-Origin": "*",
          //"Access-Control-Allow-Methods": "POST",
          "Content-Type": "application/json"
        },
        body: "{\"trackId\":\"" + trackId + "\"}");
    if (response.statusCode == httpOk) {
      dynamic body = jsonDecode(Utf8Decoder().convert(response.bodyBytes))!;
      Track track = Track.fromJson(body);
      return track;
    } else if (response.statusCode == httpLocked) {
      //No song playing
      return null;
    } else {
      throw NetworkException();
    }
  }
}
