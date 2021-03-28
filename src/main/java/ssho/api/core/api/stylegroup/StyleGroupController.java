package ssho.api.core.api.stylegroup;

import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.stylegroup.StyleGroup;
import ssho.api.core.domain.tutoriallog.TutorialLog;
import ssho.api.core.service.stylegroup.StyleGroupServiceImpl;

import java.util.List;

@RequestMapping("/style-group")
@RestController
public class StyleGroupController {

    private StyleGroupServiceImpl styleGroupService;

    public StyleGroupController(StyleGroupServiceImpl styleGroupService) {
        this.styleGroupService = styleGroupService;
    }

    @PostMapping("/initial")
    public List<TutorialLog> saveInitial(@RequestParam("userId") int userId, @RequestBody List<TutorialLog> tutorialLogList) {
        styleGroupService.saveInitial(userId, tutorialLogList);
        return tutorialLogList;
    }

    @PostMapping("")
    public void updateStyleGroup() {
        styleGroupService.updateStyleGroup();
    }

    @GetMapping("")
    public StyleGroup getByUserId(@RequestParam("userId") int userId) {
        return styleGroupService.styleGroupByUserId(userId);
    }
}
