package net.emcniece.cordova;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WifiInfo extends CordovaPlugin {

    private static final String TAG = "WifiInfo";

    WifiManager.MulticastLock lock;

    private List<InetAddress> addresses;
    private List<InetAddress> ipv6Addresses;
    private List<InetAddress> ipv4Addresses;
    private String hostname;

    public static final String ACTION_GET_INFO = "getConnectedSSID";

    // Re-initialize
    public static final String ACTION_REINIT = "reInit";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Context context = this.cordova.getActivity().getApplicationContext();
        WifiManager wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);
        lock = wifi.createMulticastLock("WifiInfoPluginLock");
        lock.setReferenceCounted(true);
        lock.acquire();

        Log.v(TAG, "Initialized");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (lock != null) {
            lock.release();
            lock = null;
        }

        Log.v(TAG, "Destroyed");
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {

        if (ACTION_GET_INFO.equals(action)) {
            Log.d(TAG, "getInfo");

            final CordovaInterface cd = this.cordova;
            cordova.getThreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    Context context = cd.getActivity().getApplicationContext();
                    WifiManager wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);

                    DhcpInfo dhcpInfo = wifi.getDhcpInfo();
                    android.net.wifi.WifiInfo wifiInfo = wifi.getConnectionInfo();

                    DhcpInfo dhcp = wifi.getDhcpInfo();

                    JSONObject status;
                    try {
                        status = jsonifyConnection(wifiInfo);

                        int ip = dhcp.gateway;

                        status.put("gateway", String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8) & 0xff, (ip >> 16) & 0xff, (ip >> 24) & 0xff));

                        Log.d(TAG, "Sending result: " + status.toString());

                        PluginResult result = new PluginResult(PluginResult.Status.OK, status);
                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                        callbackContext.error("Error: " + e.getMessage());
                    }
                }
            });

        } else {
            Log.e(TAG, "Invalid action: " + action);
            callbackContext.error("Invalid action: " + action);
            return false;
        }

        return true;
    }

    // return IP4 & IP6 addresses
    private static JSONObject jsonifyConnection(android.net.wifi.WifiInfo info) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("bssid", info.getBSSID());
        obj.put("ssid", info.getSSID());

        return obj;
    }
}
