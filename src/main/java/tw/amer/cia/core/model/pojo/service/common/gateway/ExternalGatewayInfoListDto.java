package tw.amer.cia.core.model.pojo.service.common.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalGatewayInfoListDto implements Serializable {

    @JsonProperty("EXTERNAL_GATEWAY_INFO_LIST")
    private List<ExternalGatewayInfoDto> externalGatewayInfoDtoList;
}
