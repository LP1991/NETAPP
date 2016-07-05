package com.cloudvision.tanzhenv2.order.wifi.ping.connectivity;

import java.net.URL;

public interface IRemoteResource {
    String getProtocol();
    String getPath();
    IRemoteDestination getDestination();
    URL getURL();
}
