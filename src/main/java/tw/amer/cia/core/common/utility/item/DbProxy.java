package tw.amer.cia.core.common.utility.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbProxy implements Serializable {
    private String proxyHost;
    private Integer proxyPort;
    private boolean requireAuth;
    private String proxyAccount;
    private String proxyPassword;
}