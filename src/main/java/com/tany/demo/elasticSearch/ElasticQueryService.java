package com.tany.demo.elasticSearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;


import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.index.query.SpanQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

public class ElasticQueryService {
    private TransportClient client;
    private final String clusterName="my-application";
    private final String transportAddress="192.168.0.108";//填写正确的es服务器ip地址
    private final String transportPort="9300";//默认为9300
    private String indexName;//索引名称
    private String typeName;

    public ElasticQueryService(String indexName, String typeName){
        this.indexName = indexName;
        this.typeName = typeName;
        testBefore();
    }

    public void testBefore() {
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        // 创建client
        try {
            client = new PreBuiltTransportClient(settings);
            String[] addrs = transportAddress.split(",");
            for (String addr : addrs) {
                client.addTransportAddress(new TransportAddress(InetAddress.getByName(addr), Integer.parseInt(transportPort)));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用get查询
     */

    public void testGet() {
        GetRequestBuilder requestBuilder = client.prepareGet(indexName, typeName, "1");
        GetResponse response = requestBuilder.execute().actionGet();
        GetResponse getResponse = requestBuilder.get();
        ActionFuture<GetResponse> execute = requestBuilder.execute();
//        ListenableActionFuture<GetResponse> execute = requestBuilder.execute();
        System.out.println(response.getSourceAsString());
    }

    /**
     * 使用QueryBuilder
     * termQuery("key", obj) 完全匹配
     * termsQuery("key", obj1, obj2..)   一次匹配多个值
     * matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
     * multiMatchQuery("text", "field1", "field2"..);  匹配多个字段, field有通配符忒行
     * matchAllQuery();         匹配所有文件
     */

    public void testQueryBuilder() {
//        QueryBuilder queryBuilder = QueryBuilders.termQuery("user", "test0000");
//        QueryBuilder queryBuilder = QueryBuilders.matchQuery("user", "test0000,test0001");
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("0000", "user", "message", "id");
//        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        searchFunction(queryBuilder);

    }

    /**
     * 组合查询
     * must(QueryBuilders) :   AND
     * mustNot(QueryBuilders): NOT
     * should:                  : OR
     */
    public void testQueryBuilder2() {
        QueryBuilder queryBuilder = boolQuery()
                .must(QueryBuilders.termQuery("user", "test0000"))
                .mustNot(QueryBuilders.termQuery("user", "test0000"))
                .should(QueryBuilders.termQuery("user", "test0001"))
        ;
        searchFunction(queryBuilder);
    }

    /**
     * 只查询一个id的   （没有尝试成功，无返回结果）
     * QueryBuilders.idsQuery(String...type).ids(Collection<String> ids)
     */
    public void testIdsQuery() {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(new String[]{"id-16-34065-0000"});
        searchFunction(queryBuilder);
    }

    /**
     * 包裹查询, 高于设定分数, 不计算相关性
     */
    public void testConstantScoreQuery() {
        QueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("user", "test0000")).boost(110.0f);
        searchFunction(queryBuilder);
        // 过滤查询(废弃)
//        QueryBuilders.constantScoreQuery(FilterBuilders.termQuery("name", "kimchy")).boost(2.0f);

    }

    /**
     * disMax查询
     * 对子查询的结果做union, score沿用子查询score的最大值,
     * 广泛用于muti-field查询
     */
    public void testDisMaxQuery() {
        QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.termQuery("id", "34067"))  // 查询条件
                .add(QueryBuilders.termQuery("id", "34065"))
                .boost(1.3f)
                .tieBreaker(0.7f);
        searchFunction(queryBuilder);
    }

    /**
     * 模糊查询 (没有测试出来，不知道搞毛线的)
     * 不能用通配符, 不知道干啥用
     */
    public void testFuzzyQuery() {
        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("id", "id-16-34067-0000");
        searchFunction(queryBuilder);
    }

    /**
     * 父或子的文档查询（废弃）
     */
//    public void testChildQuery() {
//        QueryBuilder queryBuilder = QueryBuilders.hasChildQuery("sonDoc", QueryBuilders.termQuery("name", "vini"));
//        searchFunction(queryBuilder);
//    }

    /**
     * moreLikeThisQuery: 实现基于内容推荐, 支持实现一句话相似文章查询
     * {
     "more_like_this" : {
     "fields" : ["title", "content"],   // 要匹配的字段, 不填默认_all
     "like_text" : "text like this one",   // 匹配的文本
     }
     }

     percent_terms_to_match：匹配项（term）的百分比，默认是0.3

     min_term_freq：一篇文档中一个词语至少出现次数，小于这个值的词将被忽略，默认是2

     max_query_terms：一条查询语句中允许最多查询词语的个数，默认是25

     stop_words：设置停止词，匹配时会忽略停止词

     min_doc_freq：一个词语最少在多少篇文档中出现，小于这个值的词会将被忽略，默认是无限制

     max_doc_freq：一个词语最多在多少篇文档中出现，大于这个值的词会将被忽略，默认是无限制

     min_word_len：最小的词语长度，默认是0

     max_word_len：最多的词语长度，默认无限制

     boost_terms：设置词语权重，默认是1

     boost：设置查询权重，默认是1

     analyzer：设置使用的分词器，默认是使用该字段指定的分词器
     */
    public void testMoreLikeThisQuery() {
        QueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(new String[]{"id","uesr","message"},new String[]{"0001"},null)
//                .like("kimchy")
                            .minTermFreq(1)         //最少出现的次数
                            .maxQueryTerms(12) // 最多允许查询的词语
                ;
        searchFunction(queryBuilder);
    }

    /**
     * 前缀查询
     */
    public void testPrefixQuery() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("id", "id-16-34065-0000").boost(100);
        searchFunction(queryBuilder);
    }

    /**
     * 查询解析查询字符串
     */
    public void testQueryString() {
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("+d-16-34065-0000").boost(200);
        searchFunction(queryBuilder);
    }

    /**
     * 范围内查询
     */
    public void testRangeQuery() {
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery("postLong")
                .from(1531726438236L)
                .to(1531726438246L)
                .includeLower(true)     // 包含上界
                .includeUpper(true);      // 包含下届
        searchFunction(queryBuilder);
    }

    /**
     * 跨度查询
     */
    public void testSpanQueries() {
//        这个查询用于确定一个单词相对于起始位置的偏移位置，举个例子：
//        如果一个文档字段的内容是：“hello,my name is tom”，我们要检索tom，那么它的span_first最小应该是5，否则就查找不到。
//        使用的时候，只是比span_term多了一个end界定而已：
        QueryBuilder queryBuilder1 = QueryBuilders.spanFirstQuery(
                QueryBuilders.spanTermQuery("message", "0000"), 50000);     // Max查询范围的结束位置

//        这个查询主要用于确定几个span_term之间的距离，通常用于检索某些相邻的单词，避免在全局跨字段检索而干扰最终的结果。
//        查询主要由两部分组成，一部分是嵌套的子span查询，另一部分就是他们之间的最大的跨度
        QueryBuilder queryBuilder2 = QueryBuilders.spanNearQuery(QueryBuilders.spanTermQuery("name", "葫芦580娃"), 30000)
                .addClause(QueryBuilders.spanTermQuery("name", "葫芦580娃")) // Span Term Queries
                .addClause(QueryBuilders.spanTermQuery("name", "葫芦3812娃"))
                .addClause(QueryBuilders.spanTermQuery("name", "葫芦7139娃"))
//                .slop(30000)
                .inOrder(false);
//                .collectPayloads(false);

        // Span Not
//        就是排除的意思。不过它内部有几个属性，include用于定义包含的span查询；exclude用于定义排除的span查询
        QueryBuilder queryBuilder3 = QueryBuilders.spanNotQuery(
                QueryBuilders.spanTermQuery("name", "葫芦580娃"),
                QueryBuilders.spanTermQuery("home", "山西省太原市2552街道"));


        // Span Or
//        嵌套一些子查询，子查询之间的逻辑关系为 或
        QueryBuilder queryBuilder4 = QueryBuilders.spanOrQuery(QueryBuilders.spanTermQuery("name", "葫芦580娃"))
                .addClause(QueryBuilders.spanTermQuery("name", "葫芦3812娃"))
                .addClause(QueryBuilders.spanTermQuery("name", "葫芦7139娃"));

        // Span Term
//        查询内部会有多个子查询，但是会设定某个子查询优先级更高，作用更大，通过关键字little和big来指定。
        QueryBuilders.spanContainingQuery(QueryBuilders.spanTermQuery("name", "葫芦580娃"),
                QueryBuilders.spanTermQuery("home", "山西省太原市2552街道"));

        searchFunction(queryBuilder1);
    }

    /**
     * 测试子查询  (废弃)
     */
//    public void testTopChildrenQuery() {
//        QueryBuilders.hasChildQuery("tweet",
//                QueryBuilders.termQuery("user", "kimchy"))
//                .scoreMode("max");
//    }

    /**
     * 通配符查询, 支持 *
     * 匹配任何字符序列, 包括空
     * 避免* 开始, 会检索大量内容造成效率缓慢
     */
    public void testWildCardQuery() {
        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("user", "test000*");
        searchFunction(queryBuilder);
    }

    /**
     * 嵌套查询, 内嵌文档查询 相当于 select id,name,(select id,name from contacts) from account
     */
    public void testNestedQuery() {
        QueryBuilder queryBuilder = QueryBuilders.nestedQuery("location",
                boolQuery()
                        .must(QueryBuilders.matchQuery("location.lat", 0.962590433140581))
                        .must(QueryBuilders.rangeQuery("location.lon").lt(36.0000).gt(0.000)), ScoreMode.Total);


    }

    /**
     * geo_point纬度/经度对字段的支持,和 geo_shape领域,支持点、线、圆、多边形、多等。
     */
    public void testIndicesQueryBuilder () throws IOException {
        QueryBuilder queryBuilder = QueryBuilders.geoShapeQuery(
                "pin.location",
                ShapeBuilders.newMultiPoint(
                        new CoordinatesBuilder()
                                .coordinate(0, 0)
                                .coordinate(0, 10)
                                .coordinate(10, 10)
                                .coordinate(10, 0)
                                .coordinate(0, 0)
                                .build())).relation(ShapeRelation.WITHIN);

    }



    /**
     * 查询遍历抽取
     * @param queryBuilder
     */
    private void searchFunction(QueryBuilder queryBuilder) {
        SearchResponse response = client.prepareSearch("twitter")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(100).execute().actionGet();

        while(true) {
            response = client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
            int number = 0;

            for (SearchHit hit : response.getHits()) {
                number++;
                Iterator<Entry<String, Object>> iterator = hit.getSourceAsMap().entrySet().iterator();
                System.out.println("score info :" +hit.getScore());
                while(iterator.hasNext()) {
                    Entry<String, Object> next = iterator.next();
                    System.out.println(next.getKey() + ": " + next.getValue());
                    if(response.getHits().getHits().length == 0) {
                        break;
                    }
                }
            }
            System.out.println("get data size:"+number);
            break;
        }
//        testResponse(response);
    }

    /**
     * 对response结果的分析
     * @param response
     */
    public void testResponse(SearchResponse response) {
        // 命中的记录数
        long totalHits = response.getHits().totalHits;

        for (SearchHit searchHit : response.getHits()) {
            // 打分
            float score = searchHit.getScore();
            // 文章id
            int id = Integer.parseInt(searchHit.getSourceAsMap().get("id").toString());
            // title
            String title = searchHit.getSourceAsMap().get("title").toString();
            // 内容
            String content = searchHit.getSourceAsMap().get("content").toString();
            // 文章更新时间
            long updatetime = Long.parseLong(searchHit.getSourceAsMap().get("updatetime").toString());
        }
    }

    /**
     * 对结果设置高亮显示
     */
    public void testHighLighted() {
        /*  5.0 版本后的高亮设置
         * client.#().#().highlighter(hBuilder).execute().actionGet();
        HighlightBuilder hBuilder = new HighlightBuilder();
        hBuilder.preTags("<h2>");
        hBuilder.postTags("</h2>");
        hBuilder.field("user");        // 设置高亮显示的字段
        */
        // 加入查询中
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("user").postTags("<h1>").postTags("</h1>");
        SearchResponse response = client.prepareSearch("blog")
                .setQuery(QueryBuilders.matchAllQuery())
                .highlighter(highlightBuilder)
                .execute().actionGet();

        // 遍历结果, 获取高亮片段
        SearchHits searchHits = response.getHits();
        for(SearchHit hit:searchHits){
            System.out.println("String方式打印文档搜索内容:");
            System.out.println(hit.getSourceAsString());
            System.out.println("Map方式打印高亮内容");
            System.out.println(hit.getHighlightFields());

            System.out.println("遍历高亮集合，打印高亮片段:");
            Text[] text = hit.getHighlightFields().get("title").getFragments();
            for (Text str : text) {
                System.out.println(str.string());
            }
        }
    }
}
