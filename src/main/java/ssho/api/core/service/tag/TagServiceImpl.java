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
import org.springframework.stereotype.Service;
import ssho.api.core.domain.tag.model.Tag;
import ssho.api.core.domain.tagset.TagSet;

import java.io.IOException;
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
        indexRequest.id(tagSet.getTagA() + "-" + tagSet.getTagB());
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Override
    public TagSet getTagSet(TagSet tagSet) throws IOException {
        GetRequest getRequest = new GetRequest(TAG_SET_INDEX, tagSet.getTagA() + "-" + tagSet.getTagB() + "-");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), TagSet.class);
    }

    private static String getUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

