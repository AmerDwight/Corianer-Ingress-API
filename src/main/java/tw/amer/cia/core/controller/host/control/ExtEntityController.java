package tw.amer.cia.core.controller.host.control;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireExtEntityVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ExternalSystemConfigEntity;
import tw.amer.cia.core.service.database.ExternalSystemConfigEntityService;
import tw.amer.cia.core.service.host.api.HostAdminExtEntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@HostRestController
@RequireExtEntityVerifyApi
@RequestMapping("/${coriander-ingress-api.host.display-name}/external/entity")
public class ExtEntityController {

    @Autowired
    ExternalSystemConfigEntityService cExtCtlService;

    @Autowired
    HostAdminExtEntityService hostAdminExtEntityService;

    @GetMapping
    public ResponseEntity<List<ExternalSystemConfigEntity>> getAll() {
        return ResponseEntity.ok(cExtCtlService.getAll());
    }

    @GetMapping("/sample")
    public ResponseEntity<ExternalSystemConfigEntity> getSample() {
        return ResponseEntity.ok(
                ExternalSystemConfigEntity.builder()
                        .extSystemId("AP_NAME_less_than_40_char")
                        .extSystemKey("AP_KEY_less_than_20_char")
                        .build());
    }

    @GetMapping("/{entityId}")
    public ResponseEntity<ExternalSystemConfigEntity> getById(@PathVariable String entityId) {
        return cExtCtlService.getById(entityId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public void create(@RequestBody ExternalSystemConfigEntity entity) throws DataSourceAccessException {
        hostAdminExtEntityService.createOrUpdateEntityFromHost(entity);
    }

    @PutMapping("/{entityId}")
    public void update(@PathVariable String entityId, @RequestBody ExternalSystemConfigEntity entity) throws DataSourceAccessException {
        if(StringUtils.isNotBlank(entityId) && StringUtils.equalsIgnoreCase(entityId,entity.getExtSystemId())){
            hostAdminExtEntityService.createOrUpdateEntityFromHost(entity);
        }else{
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_ID_MISMATCH.getCompleteMessage()
            );
        }
    }

    @DeleteMapping("/{entityId}")
    public void delete(@PathVariable String entityId) {
        hostAdminExtEntityService.deleteEntityFromHost(entityId);
    }
}
