package ssho.api.core.repository.stylegroup;

import org.springframework.data.jpa.repository.JpaRepository;
import ssho.api.core.domain.stylegroup.StyleGroup;

import java.util.List;

public interface StyleGroupRepository extends JpaRepository<StyleGroup, Integer> {
    List<StyleGroup> findAllByTagId(String tagId);
}
