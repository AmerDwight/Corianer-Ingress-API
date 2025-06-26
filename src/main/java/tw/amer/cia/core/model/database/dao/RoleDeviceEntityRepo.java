package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.RoleDeviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RoleDeviceEntityRepo extends JpaRepository<RoleDeviceEntity, String> {

    Page<RoleDeviceEntity> findByRoleIdOrderByDeviceNameAsc(String roleId, Pageable pageable);

    List<RoleDeviceEntity> findByRoleId(String roleId);

    List<RoleDeviceEntity> findDistinctByFabIdIn(Collection<String> fabIds);
}
