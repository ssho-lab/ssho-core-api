package ssho.api.core.domain.tag;

import lombok.*;

import java.util.List;

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
    private List<Double> repVec;
}
