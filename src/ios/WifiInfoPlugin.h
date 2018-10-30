//
//  WifiInfoPlugin.h
//  WifiInfoPlugin
//
//  Created by Greg Avola 9/14/18
//
//

#import <Cordova/CDV.h>

@interface WifiInfoPlugin : CDVPlugin


- (void)getConnectedSSID:(CDVInvokedUrlCommand *)command;

@end
