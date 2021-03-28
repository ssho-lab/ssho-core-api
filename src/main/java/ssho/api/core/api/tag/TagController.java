package ssho.api.core.api.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.tagset.TagSet;
import ssho.api.core.service.tag.TagServiceImpl;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagServiceImpl tagService;

    public TagController(final TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    /**
     * 태그 저장
     * @param tagNameList
     * @throws IOException
     */
    @PostMapping("")
    public void saveAll(@RequestBody List<String> tagNameList) throws IOException {
        tagService.saveByName(tagNameList);
    }

    /**
     * 태그 전체 조회
     * @return
     */
    @GetMapping("")
    public List<Tag> getTagList() {
        return tagService.getTagList();
    }

    /**
     * 태그셋 저장
     * @param tagSet
     * @throws IOException
     */
    @PostMapping("/set")
    public void saveTagSet(@RequestBody TagSet tagSet) throws IOException {
        tagService.saveTagSet(tagSet);
    }

    /**
     * 태그셋 조회
     * @param tagSet
     * @throws IOException
     */
    @GetMapping("/set")
    public void getTagSet(@RequestBody TagSet tagSet) throws IOException {
        tagService.getTagSet(tagSet);
    }
}

