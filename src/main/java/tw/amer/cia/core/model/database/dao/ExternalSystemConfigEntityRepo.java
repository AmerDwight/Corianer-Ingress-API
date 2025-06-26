package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.ExternalSystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalSystemConfigEntityRepo extends JpaRepository<ExternalSystemConfigEntity, String> {
}
