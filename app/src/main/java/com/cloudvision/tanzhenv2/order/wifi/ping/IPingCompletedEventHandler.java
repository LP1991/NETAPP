package com.cloudvision.tanzhenv2.order.wifi.ping;

/**
 * Created by User on 13/07/2015.
 */
public interface IPingCompletedEventHandler {
    void onPingCompleted(IPingService source, IPingResult result);
}
