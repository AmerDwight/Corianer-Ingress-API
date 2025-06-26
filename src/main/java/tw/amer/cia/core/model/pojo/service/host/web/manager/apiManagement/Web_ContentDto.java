package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ContentDto implements Serializable {
    @JsonProperty("ITEM_NAME")
    private String itemName;
}
