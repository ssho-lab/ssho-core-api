package ssho.api.core.domain.tag;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Tag {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean isActive;
}
