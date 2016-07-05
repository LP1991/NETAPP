package com.cloudvision.tanzhenv2.order.wifi.ping.connectivity;

import java.net.InetAddress;

/**
 * Created by User on 13/07/2015.
 */
public interface IRemoteDestination extends IDestination {
    InetAddress getAddress();
    int getPort();
}
