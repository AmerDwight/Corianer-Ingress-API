package tw.amer.cia.core.service.host;

import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.model.database.dao.SystemProxyEntityRepo;
import tw.amer.cia.core.model.database.dao.FabProxyEntityRepo;
import tw.amer.cia.core.model.database.dao.DnsProxyReferenceConfigEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.AllProxyDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@HostService
@Slf4j
public class ProxyServiceForHost {

    @Autowired
    SystemProxyEntityRepo systemProxyEntityRepo;
    @Autowired
    DnsProxyReferenceConfigEntityRepo dnsProxyReferenceConfigEntityRepo;
    @Autowired
    FabProxyEntityRepo fabProxyEntityRepo;

    public AllProxyDataDto retrieveAllProxyData() {
        AllProxyDataDto result = new AllProxyDataDto();

        // Build Proxy Data Process
        result.setProxyList(systemProxyEntityRepo.findAll());
        result.setNetRefList(dnsProxyReferenceConfigEntityRepo.findAll());
        result.setFabProxyList(fabProxyEntityRepo.findAll());

        return result;
    }
}
