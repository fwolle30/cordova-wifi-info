/* global cordova:false */
/* globals window */

var exec = cordova.require('cordova/exec'),
    utils = cordova.require('cordova/utils');

var WifiInfo = {
    getConnectedSSID: function(successCallback, errorCallback, options) {
        exec(successCallback, errorCallback, 'WifiInfo', 'getConnectedSSID', [options]);
    }
};

module.exports = WifiInfo;
