package tw.amer.cia.core.common;

import tw.amer.cia.core.exception.DataSourceAccessException;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public class APISetting {
    public static final List<String> API_INTERFACE = Arrays.asList("HTTP", "RESTFUL");

    public enum API_TYPE {
        RETRIEVE("查詢"),
        CONTROL("控貨"),
        DETECTOR("探針"),
        MAINTAIN("設備保養"),
        MASK("光罩管理"),
        MACHINE("機台管理");

        @Getter
        private String chineseName;

        API_TYPE(String _chineseName) {
            this.chineseName = _chineseName;
        }

        public static Optional<API_TYPE> findByName(String nameString) {
            if (StringUtils.isBlank(nameString)) {
                return Optional.empty();
            }

            // 先嘗試英文名稱匹配
            try {
                return Optional.of(API_TYPE.valueOf(nameString.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // 嘗試中文名稱匹配
                return Arrays.stream(API_TYPE.values())
                        .filter(category -> StringUtils.equals(nameString, category.getChineseName()))
                        .findFirst();
            }
        }

        public static API_TYPE getByName(String nameString) throws DataSourceAccessException {
            return findByName(nameString)
                    .orElseThrow(() -> DataSourceAccessException.createExceptionForHttp(
                            HttpStatus.NOT_FOUND,
                            ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage() + API_TYPE.class.getSimpleName()
                    ));
        }
    }
}
