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
@AllArgsConstructor
@NoArgsConstructor
public class Web_SimpleCategoryAndItemPairDto implements Serializable {

    @JsonProperty("CATEGORY")
    String category;

    @JsonProperty("ITEM_LIST")
    List<String> itemList;

    public static List<Web_SimpleCategoryAndItemPairDto> importFromMap(Map<String, List<String>> dataMap) {
        List<Web_SimpleCategoryAndItemPairDto> newList = new ArrayList<>();
        for (String key : dataMap.keySet()) {
            newList.add(Web_SimpleCategoryAndItemPairDto.builder()
                    .category(key)
                    .itemList(dataMap.get(key)).build());
        }
        return newList;
    }
}
