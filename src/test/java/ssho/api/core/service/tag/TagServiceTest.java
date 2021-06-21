package ssho.api.core.service.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.tagset.TagSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TagServiceTest {

    @Autowired
    TagServiceImpl tagService;

    @Autowired
    TagSetServiceImpl tagSetService;

    @Test
    void save() throws IOException {
        List<String[]> tagNameList = Arrays.asList(new String[]{"캐주얼룩", "격식을 차리지 않는, 간편하고 경쾌한 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%BA%90%EC%A3%BC%EC%96%BC.jpg"},
                new String[]{"아메카지", "아메리칸 캐주얼, 워크웨어와 복고풍 패션의 조합이 이뤄낸 간편한 스타일의 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%95%84%EB%A9%94%EC%B9%B4%EC%A7%80.JPG"},
                new String[]{"펑크룩", "액세서리로 꾸며진 반항적이고 공격적인 스타일의 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%ED%8E%91%ED%81%AC%EB%A3%A9.JPG"},
                new String[]{"세미포멀", "다양한 TPO에서 활용 가능한 단정한 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%84%B8%EB%AF%B8%ED%8F%AC%EB%A9%80.jpg"},
                new String[]{"포멀", "공식적인 장소에서의 단정하고 격식 있는 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%98%A4%ED%94%BC%EC%8A%A4%EB%A3%A9.jpg"},
                new String[]{"스포티룩", "역동적인 활동에 적합하고 편안한 스타일의 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%8A%A4%ED%8F%AC%ED%8B%B0.JPG"},
                new String[]{"페미닌룩", "일정한 형식이 없는, 무겁지 않고 우아한 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%ED%8E%98%EB%AF%B8%EB%8B%8C.jpg"},
                new String[]{"빈티지룩", "앤티크룩, 낡은 듯 하지만 고풍스러운 느낌의 개성있는 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%8A%A4%ED%8A%B8%EB%A6%BF.JPG"},
                new String[]{"스트릿룩", "길거리에서 쉽게 접할 수 있는 유행 패션에 기반한 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%8A%A4%ED%8A%B8%EB%A6%BF.JPG"},
                new String[]{"히피룩", "평화와 사랑, 자유를 지향하는 문화에 기반한 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%8A%A4%ED%8A%B8%EB%A6%BF.JPG"},
                new String[]{"스쿨룩", "교복의 체크 패턴이나 니트, 셔츠의 디자인에 기반한 룩", "https://ssho-static.s3.ap-northeast-2.amazonaws.com/stylecard/%EC%8A%A4%EC%BF%A8%EB%A3%A9.jpg"}
                );

        List<Tag> tagList = tagService.getTagList();

        tagList = tagList.stream().map(tag -> {
            tag.setActive(false);
            for(String[] s: tagNameList) {
                if(s[0].equals(tag.getName())){
                    tag.setActive(true);
                    tag.setDescription(s[1]);
                    tag.setImageUrl(s[2]);
                    break;
                }
            }

            return tag;
        }).collect(Collectors.toList());

        tagService.save(tagList);
    }

    @Test
    void saveTagSet() throws IOException {
        String desc = "평화와 사랑, 자유를 지향하는 문화에 기반한 룩";
        String imageUrl = "https://mblogthumb-phinf.pstatic.net/MjAyMDAyMjRfMTYg/MDAxNTgyNTU1MjgyMDE4.phvmUCrzPKKOCmJ18_2EnKVUJLwcP73H_usVJdqLOkMg.i8JbhgvOyO8HWB6mi0MWOF6dwGq3DamqnTHhLgxF1kUg.JPEG.sud_inc/20200212_224804.jpg?type=w800";
        List<TagSet> tagSetList = tagSetService.getTagSetList().stream().map(tagSet -> {
            Tag tagA = tagSet.getTagA();
            Tag tagB = tagSet.getTagB();

            try {
                Tag tempA = tagService.getTagById(tagA.getId());
                tagA.setDescription(tempA.getDescription());
                tagA.setImageUrl(tempA.getImageUrl());
                tagA.setActive(tempA.isActive());
                tagA.setImageUrl(tempA.getImageUrl());

                Tag tempB = tagService.getTagById(tagB.getId());
                tagB.setDescription(tempB.getDescription());
                tagB.setImageUrl(tempB.getImageUrl());
                tagB.setActive(tempB.isActive());
                tagB.setImageUrl(tempB.getImageUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }

            tagSet.setTagA(tagA);
            tagSet.setTagB(tagB);

            return tagSet;
        }).collect(Collectors.toList());

        tagSetList.forEach(tagSet -> {
            try {
                tagSetService.saveTagSet(tagSet);
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
//        styleTagList.add("스포티룩");
//        styleTagList.add("포멀");
//        styleTagList.add("페미닌룩");
//        styleTagList.add("세미포멀");
//        styleTagList.add("스트릿룩");
//        styleTagList.add("히피룩");
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
