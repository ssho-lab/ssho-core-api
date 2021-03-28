package ssho.api.core.service.stylegroup;

import ssho.api.core.domain.stylegroup.StyleGroup;
import ssho.api.core.domain.tutoriallog.TutorialLog;

import java.util.List;

public interface StyleGroupService {
    void saveInitial(int userId, List<TutorialLog> tutorialLogList);
    void updateStyleGroup();
}
