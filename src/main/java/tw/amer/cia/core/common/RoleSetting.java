package tw.amer.cia.core.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoleSetting {
    // ROLE Type
    public enum ROLE_TYPE {
        DEPT(false),
        SYSTEM(true),
        CFT(true);
        private final boolean isMemberModifiable;

        ROLE_TYPE(boolean isMemberModifiable) {
            this.isMemberModifiable = isMemberModifiable;
        }

        public boolean isMemberModifiable() {
            return this.isMemberModifiable;
        }

        public static List<ROLE_TYPE> getModifiableRoleType() {
            return Arrays.stream(ROLE_TYPE.values())
                    .filter(ROLE_TYPE::isMemberModifiable)
                    .collect(Collectors.toList());
        }

        public static ROLE_TYPE fromString(String roleTypeStr) {
            if (roleTypeStr != null) {
                for (ROLE_TYPE type : ROLE_TYPE.values()) {
                    if (roleTypeStr.equalsIgnoreCase(type.name())) {
                        return type;
                    }
                }
            }
            return null;
        }
    }
}
