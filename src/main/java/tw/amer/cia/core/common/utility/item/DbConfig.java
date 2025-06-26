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
public class DbConfig  implements Serializable {
    private String name;
    private String dbType;
    @Builder.Default
    private String driverType="thin";
    private String address;
    private String port;
    private String sid;
    private String serverName;
    private String account;
    private String password;
}



