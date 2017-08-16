/**
 * Copyright (c) 2016 CA, Inc. All rights reserved.
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package com.ca.mas.cordova.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.ca.mas.core.service.MssoIntents;
import com.ca.mas.foundation.MAS;
import com.ca.mas.foundation.MASAuthenticationListener;
import com.ca.mas.foundation.MASCallback;
import com.ca.mas.foundation.MASConstants;
import com.ca.mas.foundation.MASOtpAuthenticationHandler;
import com.ca.mas.foundation.MASRequest;
import com.ca.mas.foundation.MASRequestBody;
import com.ca.mas.foundation.MASResponse;
import com.ca.mas.foundation.MASUser;
import com.ca.mas.foundation.auth.MASAuthenticationProvider;
import com.ca.mas.foundation.auth.MASAuthenticationProviders;
import com.ca.mas.foundation.auth.MASProximityLogin;
import com.ca.mas.foundation.auth.MASProximityLoginQRCode;
import com.ca.mas.ui.MASCustomTabs;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MASPluginMAS extends MASCordovaPlugin {
    private static final String TAG = MASPluginMAS.class.getCanonicalName();
    private Context mContext = null;
    private static final String REQUEST_CANCELLATION_MSG_KEY = "REQUEST_CANCELLATION_MSG_KEY";

    private MASOtpAuthenticationHandler masOtpAuthenticationHandlerStatic;
    private CallbackContext AUTH_LISTENER_CALLBACK;
    private CallbackContext OTP_AUTH_LISTENER_CALLBACK;
    private CallbackContext OTP_CHANNEL_SELECT_LISTENER_CALLBACK;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        mContext = webView.getContext();
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        try {
            if (action.equalsIgnoreCase("start")) {
                start(args, callbackContext);
            } else if (action.equalsIgnoreCase("startWithDefaultConfiguration")) {
                startWithDefaultConfiguration(args, callbackContext);
            } else if (action.equalsIgnoreCase("startWithJSON")) {
                startWithJSON(args, callbackContext);
            } else if (action.equalsIgnoreCase("stop")) {
                stop(args, callbackContext);
            } else if (action.equalsIgnoreCase("setConfigFileName")) {
                setConfigFileName(args, callbackContext);
            } else if (action.equalsIgnoreCase("setGrantFlow")) {
                setGrantFlow(args, callbackContext);
            } else if (action.equalsIgnoreCase("cancelRequest")) {
                cancelRequest(args, callbackContext);
            } else if (action.equalsIgnoreCase("setAuthenticationListener")) {
                setAuthenticationListener(args, callbackContext);
            } else if (action.equalsIgnoreCase("generateAndSendOTP")) {
                generateAndSendOTP(args, callbackContext);
            } else if (action.equalsIgnoreCase("validateOTP")) {
                validateOTP(args, callbackContext);
            } else if (action.equalsIgnoreCase("setOTPAuthenticationListener")) {
                setOTPAuthenticationListener(args, callbackContext);
            } else if (action.equalsIgnoreCase("setOTPChannelSelectorListener")) {
                setOTPChannelSelectorListener(args, callbackContext);
            } else if (action.equalsIgnoreCase("cancelOTPValidation")) {
                cancelOTPValidation(args, callbackContext);
            } else if (action.equalsIgnoreCase("cancelGenerateAndSendOTP")) {
                cancelGenerateAndSendOTP(args, callbackContext);
            } else if (action.equalsIgnoreCase("gatewayIsReachable")) {
                gatewayIsReachable(args, callbackContext);
            } else if (action.equalsIgnoreCase("useNativeMASUI")) {
                useNativeMASUI(args, callbackContext);
            } else if (action.equalsIgnoreCase("authorizeQRCode")) {
                authorizeQRCode(args, callbackContext);
            } else if (action.equalsIgnoreCase("completeAuthentication")) {
                completeAuthentication(args, callbackContext);
            } else if (action.equalsIgnoreCase("cancelAuthentication")) {
                cancelAuthentication(args, callbackContext);
            } else if (action.equalsIgnoreCase("getFromPath")) {
                getFromPath(args, callbackContext);
            } else if (action.equalsIgnoreCase("deleteFromPath")) {
                deleteFromPath(args, callbackContext);
            } else if (action.equalsIgnoreCase("postToPath")) {
                postToPath(args, callbackContext);
            } else if (action.equalsIgnoreCase("putToPath")) {
                putToPath(args, callbackContext);
            } else if (action.equalsIgnoreCase("doSocialLogin")) {
                doSocialLogin(args, callbackContext);
            } else {
                callbackContext.error("Invalid action");
                return false;
            }
        } catch (Throwable th) {
            callbackContext.error(getError(th));
        }
        return true;
    }

    private void start(final JSONArray args, final CallbackContext callbackContext) {
        try {
            MAS.start(mContext, true);
            String result = "Start Complete";
            success(callbackContext, result, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void startWithDefaultConfiguration(final JSONArray args, final CallbackContext callbackContext) {//TODO: Change the contract
        try {
            boolean shouldUseDefault = args.optBoolean(0, true);
            MAS.start(mContext, shouldUseDefault);
            String result = "Start complete";
            success(callbackContext, result, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void startWithJSON(final JSONArray args, final CallbackContext callbackContext) {
        try {
            JSONObject jsonObject = args.optJSONObject(0);
            if (jsonObject == null) {
                MASCordovaException e = new MASCordovaException("Invalid configuration JSON provided");
                callbackContext.error(getError(e));
                return;
            }
            MAS.start(mContext, jsonObject);
            success(callbackContext, true, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void stop(final JSONArray args, final CallbackContext callbackContext) {
        try {
            MAS.stop();
            String result = "Stop Complete";
            success(callbackContext, result, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void setConfigFileName(final JSONArray args, final CallbackContext callbackContext) {
        try {
            String filename = args.getString(0);
			if (!filename.endsWith(".json")) {
                filename = filename + ".json";
            }
            AssetManager mg = mContext.getResources().getAssets();
            try {
                mg.open(filename);
            } catch (IOException e) {
                MASCordovaException exception = new MASCordovaException("File not found", e);
                Log.e(TAG, exception.getMessage(), exception);
                callbackContext.error(getError(exception));
                return;
            }
            MAS.setConfigurationFileName(filename);
            String result = "Config file name is set";
            success(callbackContext, result, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void setGrantFlow(final JSONArray args, final CallbackContext callbackContext) {
        try {
            int grantFlow = args.getInt(0);
            switch (grantFlow) {
                case 0:
                    grantFlow = MASConstants.MAS_GRANT_FLOW_CLIENT_CREDENTIALS;
                    break;
                case 1:
                    grantFlow = MASConstants.MAS_GRANT_FLOW_PASSWORD;
                    break;
                default:
                    throw new UnsupportedOperationException("No such flow present");
            }

            MAS.setGrantFlow(grantFlow);
            String result = "Grant flow is set";
            success(callbackContext, result, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void cancelRequest(final JSONArray args, final CallbackContext callbackContext) {
        try {
            String requestId = args.getString(0);
            MAS.cancelRequest(Long.getLong(requestId));
            success(callbackContext, true, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void setAuthenticationListener(final JSONArray args, final CallbackContext callbackContext) {
        AUTH_LISTENER_CALLBACK = callbackContext;
        try {
            MAS.setAuthenticationListener(new MASAuthenticationListener() {
                @Override
                public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders masAuthenticationProviders) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        MASProximityLogin qrcode = new MASProximityLoginQRCode() {
                            @Override
                            protected void onAuthCodeReceived(String code) {
                                super.onAuthCodeReceived(code);
                                MASUser.login(null);
                                String data = "qrCodeAuthorizationComplete";
                                success(AUTH_LISTENER_CALLBACK, data, true);
                            }

                            @Override
                            public void close() {
                                super.close();
                                String data = "removeQRCode";
                                success(AUTH_LISTENER_CALLBACK, data, true);
                            }
                        };
                        MASUtil.setQrCode(qrcode);
                        JSONArray providerIds = MASUtil.setAuthenticationProviders(masAuthenticationProviders);
                        boolean init = qrcode.init((Activity) context, requestId, masAuthenticationProviders);
                        String encodedImage = "";
                        if (init) {
                            ImageView image = (ImageView) qrcode.render();
                            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrOutStream);
                            byte byteImgArr[] = byteArrOutStream.toByteArray();
                            encodedImage = Base64.encodeToString(byteImgArr, Base64.DEFAULT);
                        }
                        jsonObject.put("requestType", "Login");
                        jsonObject.put("requestId", requestId);
                        jsonObject.put("qrCodeImageBase64", encodedImage);
                        jsonObject.put("providers", providerIds);
                        qrcode.start();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    success(AUTH_LISTENER_CALLBACK, jsonObject, true);
                }

                @Override
                public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler masOtpAuthenticationHandler) {
                    masOtpAuthenticationHandlerStatic = masOtpAuthenticationHandler;
                    try {
                        JSONObject jsonObject = new JSONObject();
                        if (masOtpAuthenticationHandler.isInvalidOtp()) {
                            jsonObject.put("isInvalidOtp", "true");
                            jsonObject.put("errorMessage", "Otp is invalid");
                            success(OTP_AUTH_LISTENER_CALLBACK, jsonObject, true);
                        } else {
                            JSONArray jsonArray = new JSONArray();
                            List<String> channels = masOtpAuthenticationHandler.getChannels();
                            if (channels != null) {
                                StringBuffer channelResult = new StringBuffer();
                                for (int i = 0; i < channels.size(); i++) {
                                    channelResult.append(channels.get(i));
                                    jsonArray.put(channels.get(i));
                                    if (i != channels.size() - 1) {
                                        channelResult.append(",");
                                    }
                                }
                            }
                            success(OTP_CHANNEL_SELECT_LISTENER_CALLBACK, jsonArray, true);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                        if (masOtpAuthenticationHandler.isInvalidOtp()) {
                            OTP_AUTH_LISTENER_CALLBACK.error(getError(e));
                        } else {
                            OTP_CHANNEL_SELECT_LISTENER_CALLBACK.error(getError(e));
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, getError(e));
            pluginResult.setKeepCallback(true);
            AUTH_LISTENER_CALLBACK.sendPluginResult(pluginResult);
        }
    }

    private void generateAndSendOTP(final JSONArray args, final CallbackContext callbackContext) {
        try {
            JSONArray channels = args.getJSONArray(0);
            StringBuilder channelResult = new StringBuilder();
            for (int i = 0; i < channels.length(); i++) {
                channelResult.append(channels.get(i));
                if (i != channels.length() - 1) {
                    channelResult.append(",");
                }
            }

            masOtpAuthenticationHandlerStatic.deliver(channelResult.toString(), new MASCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    success(callbackContext, "true", false);
                }

                @Override
                public void onError(Throwable e) {
                    callbackContext.error(getError(e));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void validateOTP(final JSONArray args, final CallbackContext callbackContext) {
        try {
            String otp = args.getString(0);
            masOtpAuthenticationHandlerStatic.proceed(mContext, otp);
            success(callbackContext, true, false);// TODO: Recheck
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void setOTPAuthenticationListener(final JSONArray args, final CallbackContext callbackContext) {
        OTP_AUTH_LISTENER_CALLBACK = callbackContext;
    }

    private void setOTPChannelSelectorListener(final JSONArray args, final CallbackContext callbackContext) {
        OTP_CHANNEL_SELECT_LISTENER_CALLBACK = callbackContext;
    }

    private void cancelOTPValidation(final JSONArray args, final CallbackContext callbackContext) {
        try {
            masOtpAuthenticationHandlerStatic.cancel();
            success(callbackContext, true, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void cancelGenerateAndSendOTP(final JSONArray args, final CallbackContext callbackContext) {
        try {
            masOtpAuthenticationHandlerStatic.cancel();
            success(callbackContext, true, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void gatewayIsReachable(final JSONArray args, final CallbackContext callbackContext) {
        try {
            MAS.gatewayIsReachable(new MASCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    success(callbackContext, result, false);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage(), e);
                    callbackContext.error(getError(e));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private void useNativeMASUI(final JSONArray args, final CallbackContext callbackContext) {
        MAS.setAuthenticationListener(new MASAuthenticationListener() {
            @Override
            public void onAuthenticateRequest(Context context, long requestId, MASAuthenticationProviders providers) {
                Intent loginIntent = getLoginIntent(context, requestId, providers);
                if (loginIntent != null) {
                    ((Activity) context).startActivity(loginIntent);
                }
            }

            @Override
            public void onOtpAuthenticateRequest(Context context, MASOtpAuthenticationHandler handler) {
                Intent otpIntent = getOtpIntent(context, handler);
                if (otpIntent != null) {
                    ((Activity) context).startActivity(otpIntent);
                }
            }
        });
        success(callbackContext, true, false);
    }

    private Intent getLoginIntent(Context context, long requestID, MASAuthenticationProviders providers) {
        try {
            Class<?> c = Class.forName("com.ca.mas.ui.MASLoginActivity");
            Intent loginIntent = new Intent(context, c);
            loginIntent.putExtra(MssoIntents.EXTRA_AUTH_PROVIDERS, providers);
            loginIntent.putExtra(MssoIntents.EXTRA_REQUEST_ID, requestID);
            return loginIntent;
        } catch (Exception e) {
            return null;
        }
    }

    private Intent getOtpIntent(Context context, MASOtpAuthenticationHandler handler) {
        try {
            Class<?> c = Class.forName("com.ca.mas.ui.otp.MASOtpActivity");
            Intent otpIntent = new Intent(context, c);
            otpIntent.putExtra(MssoIntents.EXTRA_OTP_HANDLER, handler);
            return otpIntent;
        } catch (Exception e) {
            return null;
        }
    }

    private void authorizeQRCode(final JSONArray args, final CallbackContext callbackContext) {
        String url;
        try {
            url = (String) args.get(0);
        } catch (JSONException e) {
            callbackContext.error(getError(e));
            return;
        }

        MASProximityLoginQRCode.authorize(url, new MASCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                String msg = "QR Code authorized successfully!";
                success(callbackContext, msg, false);
            }


            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                callbackContext.error(getError(e));
            }
        });
    }

    private void completeAuthentication(final JSONArray args, final CallbackContext callbackContext) {
        String username = null;
        String password = null;
        try {
            username = (String) args.get(0);
            password = (String) args.get(1);
        } catch (JSONException e) {
            callbackContext.error(getError(new MASCordovaException("Invalid credentials provided")));
            return;
        }
        MASUser.login(username, password.toCharArray(), new MASCallback<MASUser>() {
            @Override
            public void onSuccess(MASUser masUser) {
                success(callbackContext, true);
                MASUtil.getQrCode().stop();
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, error.getMessage(), error);
                callbackContext.error(getError(error));

            }
        });
    }

    private void doSocialLogin(final JSONArray args, final CallbackContext callbackContext) {
        String providerName = null;
        try {
            providerName = (String) args.get(0);
        } catch (JSONException e) {
            callbackContext.error(getError(new MASCordovaException("Invalid provider name provided")));
            return;
        }

        MASAuthenticationProvider provider = MASUtil.getProvider(providerName);
        MASCustomTabs.socialLogin(mContext, provider, new MASCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                success(callbackContext, true, false);
            }

            @Override
            public void onError(Throwable e) {
                callbackContext.error(getError(e));
            }
        });
    }

    private void cancelAuthentication(final JSONArray args, final CallbackContext callbackContext) {
        try {
            int requestId = args.getInt(0);
            if (requestId == 0) {
                Log.e(TAG, "request Id is empty");
                callbackContext.error("request Id is  empty");
            }
            String cancellationMessage = args.optString(1, "Request cancelled");
            MASUtil.getQrCode().stop();
            Bundle bundle = new Bundle();
            bundle.putString(REQUEST_CANCELLATION_MSG_KEY, cancellationMessage);
            MAS.cancelRequest(requestId, bundle);
            success(callbackContext, true, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callbackContext.error(getError(e));
        }
    }

    private abstract class InvokeCommand {
        protected static final int MAS_REQUEST_RESPONSE_TYPE_JSON = 0;
        protected static final int MAS_REQUEST_RESPONSE_TYPE_SCIM_JSON = 1;
        protected static final int MAS_REQUEST_RESPONSE_TYPE_TEXT_PLAIN = 2;
        protected static final int MAS_REQUEST_RESPONSE_TYPE_WWW_FORM_URL_ENCODED = 3;
        protected static final int MAS_REQUEST_RESPONSE_TYPE_XML = 4;

        private static final int PATH = 0;
        private static final int PARAMETERS = 1;
        private static final int HEADERS = 2;
        private static final int REQUEST_TYPE = 3;


        protected void execute(Context context, JSONArray args, final CallbackContext callbackContext) {
            try {
                JSONObject parameters = args.optJSONObject(PARAMETERS);
                JSONObject headers = args.optJSONObject(HEADERS);
                String path = args.getString(PATH);
                final int requestType = args.getInt(REQUEST_TYPE);
                MASRequest.MASRequestBuilder builder = getRequestBuilder(path, parameters, requestType);
                if (headers != null && headers.names() != null) {
                    for (int i = 0; i < headers.names().length(); i++) {
                        String name = headers.names().getString(i);
                        String value = headers.getString(name);
                        builder.header(name, value);
                    }
                }
                builder.notifyOnCancel();
                MAS.invoke(builder.build(), new MASCallback<MASResponse<Object>>() {

                    @Override
                    public void onSuccess(MASResponse masResponse) {
                        JSONObject response = new JSONObject();
                        Object content = masResponse.getBody().getContent();
                        if (content != null) {
                            try {
                                response.put("MASResponseInfoBodyInfoKey", content);
                            } catch (JSONException ignore) {
                            }
                        }
                        Map<String, List<String>> responseHeaders = masResponse.getHeaders();
                        if (responseHeaders != null) {
                            JSONObject headerJson = new JSONObject();
                            for (String h : responseHeaders.keySet()) {
                                List<String> hv = responseHeaders.get(h);
                                if (hv != null && !hv.isEmpty()) {
                                    try {
                                        headerJson.put(h, hv.get(0));
                                    } catch (JSONException ignore) {
                                    }
                                }
                            }
                            try {
                                response.put("MASResponseInfoHeaderInfoKey", headerJson);
                            } catch (JSONException ignore) {
                            }
                        }
                        success(callbackContext, response, false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof MAS.RequestCancelledException) {
                            JSONObject error = new JSONObject();
                            String errorMessage = "Request Cancelled";
                            try {
                                if ((((MAS.RequestCancelledException) throwable).getData() != null &&
                                        ((MAS.RequestCancelledException) throwable).getData().get(REQUEST_CANCELLATION_MSG_KEY) != null)) {
                                    errorMessage = (String) ((MAS.RequestCancelledException) throwable).getData().get(REQUEST_CANCELLATION_MSG_KEY);
                                }
                                error.put("errorMessage", errorMessage);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            callbackContext.error(error);
                        } else
                            callbackContext.error(getError(throwable));
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                callbackContext.error(getError(e));
            }
        }

        protected Uri getUri(String path, JSONObject parameters) throws JSONException {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.encodedPath(path);
            if (parameters != null && parameters.names() != null) {
                for (int i = 0; i < parameters.names().length(); i++) {
                    String name = parameters.names().getString(i);
                    String value = parameters.getString(name);
                    uriBuilder.appendQueryParameter(name, value);
                }
            }
            return uriBuilder.build();
        }

        public abstract MASRequest.MASRequestBuilder getRequestBuilder(String path, JSONObject parameters, int requestType) throws Exception;
    }

    private void getFromPath(final JSONArray args, final CallbackContext callbackContext) {
        InvokeCommand command = new InvokeCommand() {
            @Override
            public MASRequest.MASRequestBuilder getRequestBuilder(String path, JSONObject parameters, int requestType) throws Exception {
                return new MASRequest.MASRequestBuilder(getUri(path, parameters)).get();
            }
        };
        command.execute(mContext, args, callbackContext);
    }

    private void deleteFromPath(final JSONArray args, final CallbackContext callbackContext) {
        InvokeCommand command = new InvokeCommand() {
            @Override
            public MASRequest.MASRequestBuilder getRequestBuilder(String path, JSONObject parameters, int requestType) throws Exception {
                return new MASRequest.MASRequestBuilder(getUri(path, parameters)).delete(null);
            }
        };
        command.execute(mContext, args, callbackContext);
    }

    private void postToPath(final JSONArray args, final CallbackContext callbackContext) {
        InvokeCommand command = new InvokeCommand() {
            @Override
            public MASRequest.MASRequestBuilder getRequestBuilder(String path, JSONObject parameters, int requestType) throws Exception {
                MASRequest.MASRequestBuilder builder = new MASRequest.MASRequestBuilder(new URI(path));
                if (parameters != null) {
                    switch (requestType) {
                        case MAS_REQUEST_RESPONSE_TYPE_JSON:
                        case MAS_REQUEST_RESPONSE_TYPE_SCIM_JSON:
                            builder.post(MASRequestBody.jsonBody(parameters));
                            break;
                        case MAS_REQUEST_RESPONSE_TYPE_TEXT_PLAIN:
                        case MAS_REQUEST_RESPONSE_TYPE_XML:
                            if (parameters.names().length() > 0) {
                                builder.post(MASRequestBody.stringBody(parameters.getString(parameters.names().getString(0))));
                            }
                            break;
                        case MAS_REQUEST_RESPONSE_TYPE_WWW_FORM_URL_ENCODED:
                            List<Pair<String, String>> list = new ArrayList<>();
                            for (int i = 0; i < parameters.names().length(); i++) {
                                String name = parameters.names().getString(i);
                                String value = parameters.getString(name);
                                list.add(new Pair<>(name, value));
                            }
                            builder.post(MASRequestBody.urlEncodedFormBody(list));
                            break;
                    }
                }
                return builder;
            }
        };
        command.execute(mContext, args, callbackContext);
    }

    private void putToPath(final JSONArray args, final CallbackContext callbackContext) {
        InvokeCommand command = new InvokeCommand() {
            @Override
            public MASRequest.MASRequestBuilder getRequestBuilder(String path, JSONObject parameters, int requestType) throws Exception {
                MASRequest.MASRequestBuilder builder = new MASRequest.MASRequestBuilder(new URI(path));
                if (parameters != null) {
                    switch (requestType) {
                        case MAS_REQUEST_RESPONSE_TYPE_JSON:
                        case MAS_REQUEST_RESPONSE_TYPE_SCIM_JSON:
                            builder.put(MASRequestBody.jsonBody(parameters));
                            break;
                        case MAS_REQUEST_RESPONSE_TYPE_TEXT_PLAIN:
                        case MAS_REQUEST_RESPONSE_TYPE_XML:
                            if (parameters.names().length() > 0) {
                                builder.put(MASRequestBody.stringBody(parameters.getString(parameters.names().getString(0))));
                            }
                            break;
                        case MAS_REQUEST_RESPONSE_TYPE_WWW_FORM_URL_ENCODED:
                            List<Pair<String, String>> list = new ArrayList<>();
                            for (int i = 0; i < parameters.names().length(); i++) {
                                String name = parameters.names().getString(i);
                                String value = parameters.getString(name);
                                list.add(new Pair<>(name, value));
                            }
                            builder.put(MASRequestBody.urlEncodedFormBody(list));
                            break;
                    }
                }
                return builder;
            }
        };
        command.execute(mContext, args, callbackContext);
    }
}