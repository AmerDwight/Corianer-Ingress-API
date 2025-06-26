package tw.amer.cia.core.model.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GwRouteDto
{

    @JsonProperty("GW_ROUTE_ID")
    private String gwRouteId;

    @JsonProperty("FAB_ID")
    private String fabId;

    @JsonProperty("ENDPOINT_ID")
    private String endpointId;
}
