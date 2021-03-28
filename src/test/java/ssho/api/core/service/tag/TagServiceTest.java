package ssho.api.core.service.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.tagset.TagSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TagServiceTest {

    @Autowired
    TagServiceImpl tagService;

    @Test
    void save() throws IOException {
        String desc = "평화와 사랑, 자유를 지향하는 문화에 기반한 룩";
        String imageUrl = "https://mblogthumb-phinf.pstatic.net/MjAyMDAyMjRfMTYg/MDAxNTgyNTU1MjgyMDE4.phvmUCrzPKKOCmJ18_2EnKVUJLwcP73H_usVJdqLOkMg.i8JbhgvOyO8HWB6mi0MWOF6dwGq3DamqnTHhLgxF1kUg.JPEG.sud_inc/20200212_224804.jpg?type=w800";
        List<Tag> tagList = tagService.getTagList().stream().map(tag -> {
            tag.setDescription(desc);
            tag.setImageUrl(imageUrl);
            return tag;
        }).collect(Collectors.toList());

        tagService.save(tagList);
    }

    @Test
    void saveTagSet() throws IOException {
        String desc = "평화와 사랑, 자유를 지향하는 문화에 기반한 룩";
        String imageUrl = "https://mblogthumb-phinf.pstatic.net/MjAyMDAyMjRfMTYg/MDAxNTgyNTU1MjgyMDE4.phvmUCrzPKKOCmJ18_2EnKVUJLwcP73H_usVJdqLOkMg.i8JbhgvOyO8HWB6mi0MWOF6dwGq3DamqnTHhLgxF1kUg.JPEG.sud_inc/20200212_224804.jpg?type=w800";
        List<TagSet> tagSetList = tagService.getTagSetList().stream().map(tagSet -> {
            Tag tagA = tagSet.getTagA();
            Tag tagB = tagSet.getTagB();

            tagA.setDescription(desc);
            tagA.setImageUrl(imageUrl);

            tagB.setDescription(desc);
            tagB.setImageUrl(imageUrl);

            tagSet.setTagA(tagA);
            tagSet.setTagB(tagB);

            return tagSet;
        }).collect(Collectors.toList());

        tagSetList.forEach(tagSet -> {
            try {
                tagService.saveTagSet(tagSet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

//        List<String> styleTagList = new ArrayList<>();
//
//        styleTagList.add("캐주얼룩");
//        styleTagList.add("빈티지룩");
//        styleTagList.add("스쿨룩");
//        styleTagList.add("아메카지");
//        styleTagList.add("펑크룩");
//        styleTagList.add("히피룩");
//        styleTagList.add("스포티룩");
//        styleTagList.add("포멀");
//        styleTagList.add("페미닌룩");
//        styleTagList.add("세미포멀");
//        styleTagList.add("스트릿룩");
//
//        double[][] rate = {
//                {0.0, 0.3, 0.2, 0.6, 0.3, 0.3, 0.6, 0.3, 0.4, 0.6, 0.7},
//                {0.3, 0.0, 0.3, 0.4, 0.4, 0.6, 0.4, 0.1, 0.4, 0.3, 0.5},
//                {0.2, 0.3, 0.0, 0.2, 0.3, 0.1, 0.4, 0.1, 0.4, 0.2, 0.4},
//                {0.6, 0.4, 0.2, 0.0, 0.1, 0.1, 0.5, 0.1, 0.1, 0.2, 0.7},
//                {0.3, 0.4, 0.3, 0.1, 0.0, 0.2, 0.2, 0.1, 0.1, 0.1, 0.6},
//                {0.2, 0.6, 0.1, 0.3, 0.2, 0.0, 0.1, 0.1, 0.2, 0.1, 0.5},
//                {0.6, 0.3, 0.4, 0.5, 0.2, 0.1, 0.0, 0.1, 0.1, 0.2, 0.7},
//                {0.3, 0.1, 0.1, 0.2, 0.1, 0.1, 0.1, 0.0, 0.6, 0.7, 0.1},
//                {0.4, 0.4, 0.3, 0.1, 0.3, 0.4, 0.4, 0.7, 0.0, 0.6, 0.4},
//                {0.5, 0.3, 0.2, 0.2, 0.2, 0.3, 0.2, 0.6, 0.7, 0.0, 0.5},
//                {0.7, 0.6, 0.5, 0.6, 0.4, 0.4, 0.7, 0.3, 0.4, 0.5, 0.0}
//        };
//
//        List<Tag> allTagList = tagService.getTagList();
//        List<Tag> tagList = new ArrayList<>();
//        for(String st: styleTagList) {
//            tagList.add(allTagList.stream().filter(tag -> tag.getName().equals(st)).findFirst().get());
//        }
//
//        for(int i = 0; i < styleTagList.size(); i++) {
//            Tag tagA = tagList.get(i);
//            for(int j = 0; j < styleTagList.size(); j++) {
//                if(i==j) continue;
//
//                Tag tagB = tagList.get(j);
//
//                TagSet tagSet = new TagSet();
//                tagSet.setTagA(tagA);
//                tagSet.setTagB(tagB);
//                tagSet.setRate(rate[i][j]);
//
//                tagService.saveTagSet(tagSet);
//            }
//        }
    }
}
