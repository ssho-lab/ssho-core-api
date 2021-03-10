package ssho.api.core.domain.user.model.req;

import lombok.Data;
import ssho.api.core.domain.user.model.Gender;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class UserModificationReq {

    private String name;
    private String birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
