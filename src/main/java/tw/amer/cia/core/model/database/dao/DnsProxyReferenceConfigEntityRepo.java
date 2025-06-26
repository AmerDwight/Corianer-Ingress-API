package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.DnsProxyReferenceConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DnsProxyReferenceConfigEntityRepo extends JpaRepository<DnsProxyReferenceConfigEntity, String>
{
    Optional<DnsProxyReferenceConfigEntity> findByHostname(String hostname);

}
