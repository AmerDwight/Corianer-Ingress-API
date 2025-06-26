package tw.amer.cia.core.component.structural.httpClient.proxySelector;

import java.net.Proxy;
import java.net.ProxySelector;

public abstract class DynamicProxySelector extends ProxySelector
{
    public abstract void addProxy(String reference, Proxy proxy);

    public abstract void deleteProxy(String reference, Proxy proxy);

    public abstract void addHostnameRef(String hostDomain, String reference);

    public abstract void deleteHostnameRef(String hostDomain);

}
