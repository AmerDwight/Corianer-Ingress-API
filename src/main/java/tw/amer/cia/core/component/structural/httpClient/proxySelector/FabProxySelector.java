package tw.amer.cia.core.component.structural.httpClient.proxySelector;

import tw.amer.cia.core.model.database.DnsProxyReferenceConfigEntity;
import tw.amer.cia.core.model.database.dao.SystemProxyEntityRepo;
import tw.amer.cia.core.model.database.dao.FabProxyEntityRepo;
import tw.amer.cia.core.model.database.dao.DnsProxyReferenceConfigEntityRepo;
import tw.amer.cia.core.model.database.SystemProxyEntity;
import tw.amer.cia.core.model.database.FabProxyEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
public class FabProxySelector extends DynamicProxySelector
{
    private static String DEFAULT_REF = "Default";
    private static final Integer DEFAULT_PROXY_PORT = 8080;

    @Value("${coriander-ingress-api.setting.default-proxy.enable-default}")
    boolean enableDefaultProxy;

    @Value("${coriander-ingress-api.setting.default-proxy.proxy-host}")
    String defaultProxyHost;

    @Value("${coriander-ingress-api.setting.default-proxy.proxy-port}")
    Integer defaultProxyPort;

    @Autowired
    SystemProxyEntityRepo cCoreProxyRepo;
    @Autowired
    FabProxyEntityRepo cFabProxyRepo;
    @Autowired
    DnsProxyReferenceConfigEntityRepo cNetRefRepo;

    Map<String, ArrayList<Proxy>> fabProxyMap = new ConcurrentHashMap<>();
    Map<String, String> hostnameRefMap = new ConcurrentHashMap<>();

    ArrayList<Proxy> noProxy = new ArrayList<>();

    public FabProxySelector()
    {
        // Initial
        this.noProxy.add(Proxy.NO_PROXY);
    }

    public String extractHost(String uri)
    {
        Pattern pattern = Pattern.compile("^https?://([^:/]+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }

    @PostConstruct
    public void initFabProxySelector()
    {
        // Build up proxy-need hostname reference
        List<DnsProxyReferenceConfigEntity> refList = cNetRefRepo.findAll();
        if (CollectionUtils.isNotEmpty(refList))
        {
            for (DnsProxyReferenceConfigEntity ref : refList)
            {
                if (StringUtils.isNotEmpty(ref.getProxyRef()))
                {
                    this.hostnameRefMap.put(ref.getHostname(), ref.getProxyRef());
                }
            }
        }

        // Build up proxies
        List<FabProxyEntity> fabProxyRelationList = cFabProxyRepo.findAll();
        if (CollectionUtils.isNotEmpty(fabProxyRelationList))
        {
            for (FabProxyEntity fabProxy : fabProxyRelationList)
            {
                Optional<SystemProxyEntity> onSearchProxy = cCoreProxyRepo.findByProxyId(fabProxy.getProxyId());
                if (onSearchProxy.isPresent())
                {
                    SystemProxyEntity proxyData = onSearchProxy.get();
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyData.getProxyHost(), proxyData.getProxyPort()));
                    this.addProxy(fabProxy.getFabId(), proxy);
                }
            }
        }

        if (enableDefaultProxy && StringUtils.isNotEmpty(defaultProxyHost))
        {
            ArrayList<Proxy> proxyList = new ArrayList<>();
            proxyList.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(defaultProxyHost, defaultProxyPort != null ? defaultProxyPort : DEFAULT_PROXY_PORT)));
            if(CollectionUtils.isNotEmpty(proxyList))
            {
                log.info("Proxy Selector: Initial default proxy: REF:{} for {}:{} ", DEFAULT_REF, defaultProxyHost, defaultProxyPort);
                this.fabProxyMap.put(DEFAULT_REF, proxyList);
            }
        }
    }

    @Override
    public List<Proxy> select(URI uri)
    {
        log.debug("On selecting proxy");
        String host = this.extractHost(uri.toString());
        log.debug("Proxy select by {}, From original uri: {}",host,uri);
        if (StringUtils.isNotEmpty(host))
        {
            String hostRef = hostnameRefMap.getOrDefault(host, DEFAULT_REF);
            if (StringUtils.isNotEmpty(hostRef))
            {
                log.debug("ProxySelect Ref to {}", hostRef);
            }
            List<Proxy> selectedProxy = fabProxyMap.getOrDefault(hostRef, noProxy);
            log.debug("Selected proxy: By:{} , use:{}", hostRef, selectedProxy.toString());
            return selectedProxy;
        }
        return noProxy;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe)
    {
        // Log the connection failure details
        log.error("Connection failed to {} at {} due to {}", uri, sa, ioe.getMessage(), ioe);
    }

    @Override
    public void addProxy(String fabId, Proxy proxy)
    {
        if (this.fabProxyMap.containsKey(fabId))
        {
            this.fabProxyMap.get(fabId).add(proxy);
        } else
        {
            ArrayList<Proxy> proxyList = new ArrayList<>();
            proxyList.add(proxy);
            this.fabProxyMap.put(fabId, proxyList);
        }
    }

    @Override
    public void deleteProxy(String fabId, Proxy onDeleteProxy)
    {
        if (this.fabProxyMap.containsKey(fabId))
        {
            ArrayList<Proxy> proxyList = this.fabProxyMap.get(fabId);
            proxyList.removeIf(proxy -> proxy.equals(onDeleteProxy));
        }
    }

    @Override
    public void addHostnameRef(String hostOrIp, String fabId)
    {
        this.hostnameRefMap.put(hostOrIp, fabId);
    }

    @Override
    public void deleteHostnameRef(String hostOrIp)
    {
        this.hostnameRefMap.remove(hostOrIp);
    }
}
