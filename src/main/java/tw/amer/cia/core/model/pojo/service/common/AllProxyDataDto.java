package tw.amer.cia.core.model.pojo.service.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.amer.cia.core.model.database.DnsProxyReferenceConfigEntity;
import tw.amer.cia.core.model.database.FabProxyEntity;
import tw.amer.cia.core.model.database.SystemProxyEntity;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllProxyDataDto implements Serializable {
    private List<SystemProxyEntity> proxyList;
    private List<DnsProxyReferenceConfigEntity> netRefList;
    private List<FabProxyEntity> fabProxyList;
}
