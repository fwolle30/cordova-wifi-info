
cordova-wifi-info
------------------------

Simple function for iOS only that allows you to get the SSID information from a connected device. This will only return a valid response if the user is connected to Wifi AND connected. iOS does not allow for scanning.

`cordova plugin add https://github.com/gregavola/cordova-wifi-info`


```
WifiInfo.getConnectedSSID(
  result => {
    console.log(result);
  },
  err => {
    console.error(err);
  }
);
```

The response of the plugin shuold be:

```
{
    "ssid": "SSID HERE",
    "bssid": "BSSID HERE",
}
```

Note: This will not work on a simulator - it will throw an error, but will actual allow to build. This doesn't add HotSpotConfiguration entitlements, just a simple way to get the Network Status Info from Wifi.

You could use Ionic `platform` like so and the [Network Infromation plugin](https://ionicframework.com/docs/native/network/) to get this information:

```
if (this.platform.is("cordova")) {
	if (this.platform.is('ios') && this.network.type == 'wifi') {
		WifiInfo.getConnectedSSID(
		  result => {
		    console.log(result);
		  },
		  err => {
		    console.error(err);
		  }
		);
	}
}
```
