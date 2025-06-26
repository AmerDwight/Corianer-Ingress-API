package tw.amer.cia.core.service.client;

import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.component.structural.httpClient.proxySelector.DynamicProxySelector;
import tw.amer.cia.core.model.database.DnsProxyReferenceConfigEntity;
import tw.amer.cia.core.model.database.SystemProxyEntity;
import tw.amer.cia.core.model.database.dao.SystemProxyEntityRepo;
import tw.amer.cia.core.model.database.dao.FabProxyEntityRepo;
import tw.amer.cia.core.model.database.dao.DnsProxyReferenceConfigEntityRepo;
import tw.amer.cia.core.model.database.FabProxyEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collection;
import java.util.Optional;

@ClientService
@Slf4j
public class ProxyServiceForClient
{

    @Autowired
    SystemProxyEntityRepo systemProxyEntityRepo;
    @Autowired
    DnsProxyReferenceConfigEntityRepo dnsProxyReferenceConfigEntityRepo;
    @Autowired
    FabProxyEntityRepo fabProxyEntityRepo;
    @Autowired
    DynamicProxySelector dynamicProxySelector;

    @Transactional(rollbackFor = {Exception.class})
    public boolean createProxyBatch(Collection<SystemProxyEntity> proxyCollection)
    {
        boolean procedureSuccess = true;
        if (CollectionUtils.isNotEmpty(proxyCollection))
        {
            systemProxyEntityRepo.saveAll(proxyCollection);
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean createProxyNetReferenceBatch(Collection<DnsProxyReferenceConfigEntity> netRefCollection)
    {
        boolean procedureSuccess = true;
        if (CollectionUtils.isNotEmpty(netRefCollection))
        {
            netRefCollection.forEach(
                    cNetRef ->
                    {
                        dynamicProxySelector.addHostnameRef(cNetRef.getHostname(), cNetRef.getProxyRef());
                        dnsProxyReferenceConfigEntityRepo.save(cNetRef);
                    }
            );
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean buildProxyByReference(Collection<FabProxyEntity> fabProxyCollection)
    {
        boolean procedureSuccess = true;
        if (CollectionUtils.isNotEmpty(fabProxyCollection))
        {
            fabProxyCollection.forEach(
                    cFabProxy ->
                    {
                        Optional<SystemProxyEntity> onSearchProxy = systemProxyEntityRepo.findByProxyId(cFabProxy.getProxyId());
                        if (onSearchProxy.isPresent())
                        {
                            SystemProxyEntity proxyData = onSearchProxy.get();
                            dynamicProxySelector.addProxy(cFabProxy.getFabId(),
                                    new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyData.getProxyHost(), proxyData.getProxyPort())));
                            fabProxyEntityRepo.save(cFabProxy);
                        }
                    }
            );
        }
        return procedureSuccess;
    }
}
