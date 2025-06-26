package tw.amer.cia.core.model.pojo.service.host.web.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_BasicSiteItemDto implements Serializable {

    @JsonProperty("SITE_NAME")
    String siteName;

    @JsonProperty("FAB_LIST")
    List<String> fabList;


    public static List<Web_BasicSiteItemDto> importFromMap(Map<String, List<String>> dataMap) {
        List<Web_BasicSiteItemDto> newList = new ArrayList<>();
        for (String key : dataMap.keySet()) {
            newList.add(Web_BasicSiteItemDto.builder()
                    .siteName(key)
                    .fabList(dataMap.get(key)).build());
        }
        return newList;
    }

}
