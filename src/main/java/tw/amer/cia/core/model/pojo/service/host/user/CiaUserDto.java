package tw.amer.cia.core.model.pojo.service.host.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CiaUserDto {
    private String userId;
    private String deptCode;
    private String userName;
    private String userDesc;
    private String isActive;
}