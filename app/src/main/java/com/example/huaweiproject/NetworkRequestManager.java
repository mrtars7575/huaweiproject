package com.example.huaweiproject;


import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.hms.maps.model.LatLng;

import android.util.Log;

import okhttp3.Response;

/**
 * NetworkRequestManager
 */
public class NetworkRequestManager {
    private static final String TAG = "NetworkRequestManager";

    // Maximum number of retries.
    private static final int MAX_TIMES = 10;

    /**
     * Obtaining the Results of Walking Path Planning
     *
     * @param latLng1  origin latitude and longitude
     * @param latLng2  destination latitude and longitude
     * @param listener network listener
     */
    public static void getWalkingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2,
                                                     final OnNetworkListener listener) {
        getWalkingRoutePlanningResult(latLng1, latLng2, listener, 0, false);
    }

    /**
     * Obtaining the Results of Walking Path Planning
     *
     * @param latLng1    origin latitude and longitude
     * @param latLng2    destination latitude and longitude
     * @param listener   network listener
     * @param count      last number of retries
     * @param needEncode dose the api key need to be encoded
     */
    private static void getWalkingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2,
                                                      final OnNetworkListener listener, int count, final boolean needEncode) {
        final int curCount = ++count;
        Log.e(TAG, "current count: " + curCount);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response =
                        NetClient.getNetClient().getWalkingRoutePlanningResult(latLng1, latLng2, needEncode);
                String result = "";
                String returnCode = "";
                String returnDesc = "";
                boolean need = needEncode;

                try {
                    result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    returnCode = jsonObject.optString("returnCode");
                    returnDesc = jsonObject.optString("returnDesc");

                } catch (NullPointerException e) {
                    returnDesc = "Request Fail!";
                } catch (IOException e) {
                    returnDesc = "Request Fail!";
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

                if (returnCode.equals("0")) {
                    if (null != listener) {
                        listener.requestSuccess(result);
                    }
                    return;
                }

                if (curCount >= MAX_TIMES) {
                    if (null != listener) {
                        listener.requestFail(returnDesc);
                    }
                    return;
                }

                if (returnCode.equals("6")) {
                    need = true;
                }
                getWalkingRoutePlanningResult(latLng1, latLng2, listener, curCount, need);
            }
        }).start();
    }

    /**
     * Obtaining the Results of Bicycling Path Planning
     *
     * @param latLng1  origin latitude and longitude
     * @param latLng2  destination latitude and longitude
     * @param listener network listener
     */
    public static void getBicyclingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2,
                                                       final OnNetworkListener listener) {
        getBicyclingRoutePlanningResult(latLng1, latLng2, listener, 0, false);
    }

    /**
     * Obtaining the Results of Bicycling Path Planning
     *
     * @param latLng1    origin latitude and longitude
     * @param latLng2    destination latitude and longitude
     * @param listener   network listener
     * @param count      last number of retries
     * @param needEncode dose the api key need to be encoded
     */
    private static void getBicyclingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2,
                                                        final OnNetworkListener listener, int count, final boolean needEncode) {
        final int curCount = ++count;
        Log.e(TAG, "current count: " + curCount);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response =
                        NetClient.getNetClient().getBicyclingRoutePlanningResult(latLng1, latLng2, needEncode);
                if (null != response && null != response.body() && response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (null != listener) {
                            listener.requestSuccess(result);
                        }
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                String returnCode = "";
                String returnDesc = "";
                boolean need = needEncode;

                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    returnCode = jsonObject.optString("returnCode");
                    returnDesc = jsonObject.optString("returnDesc");
                } catch (NullPointerException e) {
                    returnDesc = "Request Fail!";
                } catch (IOException e) {
                    returnDesc = "Request Fail!";
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                if (curCount >= MAX_TIMES) {
                    if (null != listener) {
                        listener.requestFail(returnDesc);
                    }
                    return;
                }

                if (returnCode.equals("6")) {
                    need = true;
                }
                getBicyclingRoutePlanningResult(latLng1, latLng2, listener, curCount, need);
            }
        }).start();
    }

    /**
     * Obtaining the Results of Driving Path Planning
     *
     * @param latLng1  origin latitude and longitude
     * @param latLng2  destination latitude and longitude
     * @param listener network listener
     */
    public static void getDrivingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2,
                                                     final OnNetworkListener listener) {
        getDrivingRoutePlanningResult(latLng1, latLng2, listener, 0, false);
    }

    /**
     * Obtaining the Results of Driving Path Planning
     *
     * @param latLng1    origin latitude and longitude
     * @param latLng2    destination latitude and longitude
     * @param listener   network listener
     * @param count      last number of retries
     * @param needEncode dose the api key need to be encoded
     */
    private static void getDrivingRoutePlanningResult(final LatLng latLng1, final LatLng latLng2,
                                                      final OnNetworkListener listener, int count, final boolean needEncode) {
        final int curCount = ++count;
        Log.e(TAG, "current count: " + curCount);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response =
                        NetClient.getNetClient().getDrivingRoutePlanningResult(latLng1, latLng2, needEncode);
                if (null != response && null != response.body() && response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (null != listener) {
                            listener.requestSuccess(result);
                        }
                        return;
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                String returnCode = "";
                String returnDesc = "";
                boolean need = needEncode;

                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    returnCode = jsonObject.optString("returnCode");
                    returnDesc = jsonObject.optString("returnDesc");
                } catch (NullPointerException e) {
                    returnDesc = "Request Fail!";
                } catch (IOException e) {
                    returnDesc = "Request Fail!";
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

                if (curCount >= MAX_TIMES) {
                    if (null != listener) {
                        listener.requestFail(returnDesc);
                    }
                    return;
                }

                if (returnCode.equals("6")) {
                    need = true;
                }
                getDrivingRoutePlanningResult(latLng1, latLng2, listener, curCount, need);
            }
        }).start();
    }

    public interface OnNetworkListener {
        void requestSuccess(String result);

        void requestFail(String errorMsg);
    }
}