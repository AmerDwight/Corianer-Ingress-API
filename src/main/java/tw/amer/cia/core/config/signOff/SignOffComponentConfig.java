package tw.amer.cia.core.config.signOff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import tw.amer.cia.core.component.functional.signOff.SignOffProcessComponent;
import tw.amer.cia.core.component.structural.annotation.HostConfiguration;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority.ApplyRoleAuthoritySingleSystemCompleteDataDto;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.ApplyRoleAuthorityRequestContext;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.ApplyRoleAuthorityResponseContext;

@Slf4j
@HostConfiguration
public class SignOffComponentConfig {
    @Bean
    public SignOffProcessComponent flowERSignOffTestComponent() {
        log.info("Undefined SignOffProcessComponent");
        return new SignOffProcessComponent() {
            // create anonymous implement
            @Override
            public ApplyRoleAuthorityRequestContext createApplyRoleAuthorityContext(ApplyRoleAuthoritySingleSystemCompleteDataDto dto) {
                return null;
            }

            @Override
            public ApplyRoleAuthorityResponseContext sendApplyRoleAuthoritySignature(ApplyRoleAuthorityRequestContext applyContext) throws DataSourceAccessException {
                return null;
            }
        };
    }
}
