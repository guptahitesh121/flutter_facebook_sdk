import 'dart:async';

import 'package:flutter/services.dart';

class FacebookUser {
  final String email;
  final String firstName;
  final String lastName;

  const FacebookUser({
    this.email,
    this.firstName,
    this.lastName,
  });

  Map<String, dynamic> toMap() {
    return {
      'email': this.email,
      'firstName': this.firstName,
      'lastName': this.lastName,
    };
  }

  factory FacebookUser.fromMap(Map map) {
    return new FacebookUser(
      email: map['email'] as String,
      firstName: map['firstName'] as String,
      lastName: map['lastName'] as String,
    );
  }
}

class FlutterFacebookSdk {
  static const MethodChannel _channel = const MethodChannel('flutter_facebook_sdk');

  static Future<FacebookUser> login() async {
    final fbUserMap = await _channel.invokeMethod('login');
    return FacebookUser.fromMap(fbUserMap);
  }

  static Future<void> share(String url, String text) async {
    await _channel.invokeMethod('share', {
      'url': url,
      'quote': text,
    });
  }
}
