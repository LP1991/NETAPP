package com.cloudvision.tanzhenv2.order.wifi.ping.config;

import java.net.URL;

import com.cloudvision.tanzhenv2.order.wifi.ping.connectivity.IRemoteDestination;
import com.cloudvision.tanzhenv2.order.wifi.ping.connectivity.IRemoteResource;
import com.cloudvision.tanzhenv2.order.wifi.ping.connectivity.RemoteResource;

public enum StaticRemoteResourcesEnum implements IRemoteResource {
    PINGS("http", RemoteDestinationsEnum.STORAGE_SERVER, "/pings/");

    private IRemoteResource _resource;

    StaticRemoteResourcesEnum(String protocol, IRemoteDestination destination, String path) {
        _resource = new RemoteResource(protocol, destination, path);
    }

    @Override
    public String getProtocol() {
        return _resource.getProtocol();
    }

    @Override
    public IRemoteDestination getDestination() {
        return _resource.getDestination();
    }

    @Override
    public String getPath() {
        return _resource.getPath();
    }

    @Override
    public URL getURL() {
        return _resource.getURL();
    }
}
