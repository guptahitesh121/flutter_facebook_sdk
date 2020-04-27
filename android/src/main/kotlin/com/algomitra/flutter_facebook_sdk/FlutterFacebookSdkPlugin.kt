package com.algomitra.flutter_facebook_sdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.NonNull
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar


class FlutterFacebookSdkPlugin : FlutterPlugin, MethodCallHandler, PluginRegistry.ActivityResultListener, ActivityAware {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    private var activity: Activity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_facebook_sdk")
        channel.setMethodCallHandler(this);
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_facebook_sdk")
            val plugin = FlutterFacebookSdkPlugin()
            plugin.activity = registrar.activity()
            channel.setMethodCallHandler(plugin)
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "login") {
            login(result)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }

    private fun login(result: Result) {
        if (activity == null) result.error("error", "Error, activity must not be null.", null)
        else {
            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email"));
            LoginManager.getInstance().registerCallback(callbackManager,
                    object : FacebookCallback<LoginResult?> {
                        override fun onSuccess(loginResult: LoginResult?) {
                            fetchProfile(result, loginResult?.accessToken!!)
                        }

                        override fun onCancel() {}

                        override fun onError(exception: FacebookException) {
                            result.error("error", exception.message, null)
                        }
                    })
        }
    }

    private fun fetchProfile(result: Result, accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObj, response ->
            if (response.error == null) {
                val map = mapOf("email" to jsonObj["email"], "firstName" to jsonObj["first_name"], "lastName" to jsonObj["last_name"])
                result.success(map)
            } else {
                result.error("error", response.error.errorMessage, null)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "email, first_name, last_name")
        request.parameters = parameters
        request.executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        return false;
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity;
        binding.removeActivityResultListener(this);
        binding.addActivityResultListener(this);
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this);
        activity = binding.activity;
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }
}
