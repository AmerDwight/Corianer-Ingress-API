package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.dao.FabEntityRepo;
import tw.amer.cia.core.model.database.FabEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class FabEntityService
{
    @Autowired
    FabEntityRepo fabEntityRepo;

    public List<FabEntity> getAllFabs()
    {
        return fabEntityRepo.findAll();
    }

    public List<FabEntity> getByCriteria(Specification<FabEntity> spec)
    {
        return fabEntityRepo.findAll(spec);
    }

    public Optional<FabEntity> getFabById(String fabId)
    {
        return fabEntityRepo.findByFabId(fabId);
    }

    public FabEntity createFab(FabEntity fab)
    {
        return fabEntityRepo.save(fab);
    }

    public FabEntity updateFab(String fabId, FabEntity fab) throws DataSourceAccessException
    {
        if (StringUtils.equals(fabId, fab.getFabId()))
        {
            return fabEntityRepo.findById(fabId).map(existingFab ->
            {
                // 更新屬性，使用 null-safe 檢查
                if (fab.getSite() != null)
                {
                    existingFab.setSite(fab.getSite());
                }
                return fabEntityRepo.save(existingFab);
            }).orElseThrow(() -> new EntityNotFoundException("Fab not found with id " + fabId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }


    public void deleteFab(String fabId)
    {
        fabEntityRepo.deleteById(fabId);
    }


}
