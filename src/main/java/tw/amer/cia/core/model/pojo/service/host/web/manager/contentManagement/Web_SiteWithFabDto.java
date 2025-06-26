package tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement;

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
public class Web_SiteWithFabDto implements Serializable {
    @JsonProperty("SITE")
    private String site;

    @JsonProperty("FAB_LIST")
    private List<String> fabList;
}
