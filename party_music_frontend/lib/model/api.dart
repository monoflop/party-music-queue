/*
 *   Copyright (C) Philipp Kutsch - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written by Philipp Kutsch <philipp@philippkutsch.com>
 */

class Api {
  final String url;
  final String key;

  Api({required this.url, required this.key});

  factory Api.fromJson(Map<String, dynamic> json) {
    return Api(
      url: json["url"]! as String,
      key: json["key"]! as String,
    );
  }
}
