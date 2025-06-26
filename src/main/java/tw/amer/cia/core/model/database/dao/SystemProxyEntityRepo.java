package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.SystemProxyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemProxyEntityRepo extends JpaRepository<SystemProxyEntity, String>
{
    Optional<SystemProxyEntity> findByProxyId(String proxyId);
}
