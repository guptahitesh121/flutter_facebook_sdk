import 'package:flutter/material.dart';
import 'package:flutter_facebook_sdk/flutter_facebook_sdk.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Facebook SDK'),
        ),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              RaisedButton(
                child: Text('Facebook Login'),
                onPressed: () async {
                  final fbUser = await FlutterFacebookSdk.login();
                  print(fbUser.toMap());
                },
              ),
              RaisedButton(
                child: Text('Facebook Share'),
                onPressed: () async {
                  await FlutterFacebookSdk.share('https://www.google.co.in/', 'This is Google');
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
