package ssho.api.core.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.tag.Tag;
import ssho.api.core.domain.tagset.TagSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private final String TAG_INDEX = "tag";
    private final String TAG_SET_INDEX = "tag-set";
    private final Integer SEARCH_SIZE = 1000;

    public TagServiceImpl(final RestHighLevelClient restHighLevelClient,
                          final ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveByName(List<String> tagNameList) throws IOException {

        for (String name : tagNameList) {
            Tag tag = new Tag();
            tag.setId(getUniqueId());
            tag.setName(name);

            IndexRequest indexRequest = new IndexRequest(TAG_INDEX).source(objectMapper.writeValueAsString(tag), XContentType.JSON);
            indexRequest.id(tag.getId());

            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void save(List<Tag> tagList) throws IOException {

        for (Tag tag : tagList) {

            IndexRequest indexRequest = new IndexRequest(TAG_INDEX).source(objectMapper.writeValueAsString(tag), XContentType.JSON);
            indexRequest.id(tag.getId());

            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public List<Tag> getTagList() {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(TAG_INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(SEARCH_SIZE);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return Stream.of(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(src -> {
                        try {
                            return objectMapper.readValue(src, Tag.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Tag getTagById(String tagId) throws IOException {
        GetRequest getRequest = new GetRequest(TAG_INDEX, tagId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), Tag.class);
    }

    @Override
    public Tag getTagByName(String tagName) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(TAG_INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", tagName));
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return Stream.of(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(src -> {
                        try {
                            return objectMapper.readValue(src, Tag.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveTagSet(TagSet tagSet) throws IOException {
        IndexRequest indexRequest = new IndexRequest(TAG_SET_INDEX).source(objectMapper.writeValueAsString(tagSet), XContentType.JSON);
        indexRequest.id(tagSet.getTagA().getId() + "-" + tagSet.getTagB().getId());
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Override
    public TagSet getTagSet(TagSet tagSet) throws IOException {
        GetRequest getRequest = new GetRequest(TAG_SET_INDEX, tagSet.getTagA().getId() + "-" + tagSet.getTagB().getId() + "-");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), TagSet.class);
    }

    @Override
    public List<TagSet> getTagSetListById(String tagId) throws IOException {

        List<Tag> tagList = getTagList();
        List<TagSet> tagSetList = new ArrayList<>();

        for(Tag tag: tagList) {
            String id = tagId + "-" + tag.getId();
            if(!tagSetExists(id)) {
                continue;
            }

            GetRequest getRequest = new GetRequest(TAG_SET_INDEX, id);
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);

            TagSet tagSet = objectMapper.readValue(getResponse.getSourceAsString(), TagSet.class);
            tagSetList.add(tagSet);
        }
        return tagSetList;
    }

    @Override
    public List<TagSet> getTagSetList() {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(TAG_SET_INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(SEARCH_SIZE);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return Stream.of(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(src -> {
                        try {
                            return objectMapper.readValue(src, TagSet.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateRepVec() {
//        List<Tag> tagList = getTagList();
//
//        tagList.stream().map(tag -> {
//            String tagId = tag.getId();
//
//        })
    }

    private boolean tagSetExists(String id) throws IOException {
        GetRequest getRequest = new GetRequest(TAG_SET_INDEX, id);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
    }

    private static String getUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

