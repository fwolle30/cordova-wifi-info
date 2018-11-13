//
//  WifiInfoPlugin.m
//  WifiInfoPlugin
//
//  Created by Greg Avola 9/14/18
//
//

#import "WifiInfoPlugin.h"
#include <ifaddrs.h>
#import <net/if.h>
#import <SystemConfiguration/CaptiveNetwork.h>

@implementation WifiInfoPlugin

#import "getgateway.h"
#import <arpa/inet.h>

- (NSString *)getGatewayIP {
    NSString *ipString = nil;
    struct in_addr gatewayaddr;
    int r = getdefaultgateway(&(gatewayaddr.s_addr));
    if(r >= 0) {
        ipString = [NSString stringWithFormat: @"%s",inet_ntoa(gatewayaddr)];
        NSLog(@"default gateway : %@", ipString );
    } else {
        NSLog(@"getdefaultgateway() failed");
    }

    return ipString;

}

- (id)fetchSSIDInfo {
    // see http://stackoverflow.com/a/5198968/907720
    NSArray *ifs = (__bridge_transfer NSArray *)CNCopySupportedInterfaces();
    NSLog(@"Supported interfaces: %@", ifs);
    NSDictionary *info;
    for (NSString *ifnam in ifs) {
        info = (__bridge_transfer NSDictionary *)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        NSLog(@"%@ => %@", ifnam, info);
        if (info && [info count]) { break; }
    }
    return info;
}

- (void)getConnectedSSID:(CDVInvokedUrlCommand*)command {
    CDVPluginResult *pluginResult = nil;
    NSDictionary *r = [self fetchSSIDInfo];
    
    if (r && [r count]) {
        NSString *ssid = [r objectForKey:(id)kCNNetworkInfoKeySSID]; //@"SSID"
        NSString *bssid = [r objectForKey:(id)kCNNetworkInfoKeyBSSID]; //@"BSSID"
        NSString *gateway = [self getGatewayIP];
        
        NSDictionary *dict = @{ @"ssid" : ssid, @"bssid" : bssid, @"gateway" : gateway};
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Not Available"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult
                                callbackId:command.callbackId];
}

@end
