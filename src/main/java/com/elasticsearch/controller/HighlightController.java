package com.elasticsearch.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elasticsearch.utils.QueryUtil;

@RestController
@RequestMapping("/highlight")
public class HighlightController {
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	/**
	 * 指定字段匹配查询 分词器：ik分词器
	 * 
	 * @param indexName-索引名称
	 * @param field-查询字段
	 * @param keyword-关键字
	 * @return
	 */
	@RequestMapping("/match/{indexName}/{field}/{keyword}")
	public String highlightForMatchQuery(@PathVariable String indexName, @PathVariable String field,
			@PathVariable String keyword) {
		// 获取当前时间
		Instant start = Instant.now();
		// 获取client对象
		Client client = elasticsearchTemplate.getClient();
		// 获取查询构造器
		QueryBuilder matchQuery = QueryBuilders.matchQuery(field, keyword).analyzer("ik_max_word");
		// 获取高亮构造器，设置字段、高亮的方式
		HighlightBuilder highlightBuilder = new HighlightBuilder().field(field)
				.preTags("<span style='color:red;font-weight:bold;'>").postTags("</span>");
		// client对象设置索引、查询器、高亮器、一次查询获取的量，返回SearchResponse对象
		SearchResponse response = client.prepareSearch(indexName).setQuery(matchQuery).highlighter(highlightBuilder)
				.setSize(1000).get();
		// 通过SearchResponse对象获取SearchHits对象
		SearchHits hits = response.getHits();
		Instant end = Instant.now();
		System.out.println("共搜索到: " + hits.getTotalHits() + " 条结果" + "," + "共耗时: "
				+ Duration.between(start, end).toMillis() + " 毫秒");
		// 通过工具类获取返回封装的查询结果
		List<Map<Object, Object>> list = QueryUtil.highlightQuery(hits);
		for (Map<Object, Object> map : list) {
			System.out.println(map.toString());
		}
		return list.toString();
	}

	/**
	 * 所有字段分词查询 分词器：ik分词器
	 * 
	 * @param indexName-索引名称
	 * @param type-类型
	 * @param keyword-关键字
	 * @return
	 */
	@RequestMapping("/string/{indexName}/{type}/{keyword}")
	public String highlightForStringQuery(@PathVariable String indexName, @PathVariable String type,
			@PathVariable String keyword) {
		// 获取当前时间
		Instant start = Instant.now();
		// 构建client对象
		Client client = elasticsearchTemplate.getClient();
		// 构建查询器
		QueryBuilder stringQuery = QueryBuilders.queryStringQuery(keyword).analyzer("ik_max_word");
		// 构建高亮器
		HighlightBuilder highlightBuilder = new HighlightBuilder();

		ClusterState cs = client.admin().cluster().prepareState().setIndices(indexName).execute().actionGet()
				.getState();
		// 根据指定索引和类型获取元数据信息
		IndexMetaData imd = cs.getMetaData().index(indexName);
		MappingMetaData mdd = imd.mapping(type);
		Map<String, Object> mapProperties = new HashMap<String, Object>();
		try {
			// 将元数据信息转化成map
			mapProperties = mdd.getSourceAsMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> fieldList = new ArrayList<String>();
		// 获取mapping里面的所有字段
		fieldList = QueryUtil.getIndexFieldList("", mapProperties);

		System.out.println("Field List:");
		for (String field : fieldList) {
			System.out.println(field);
			highlightBuilder.field(field);
		}

		highlightBuilder.preTags("<span style='color:red;font-weight:bold;'>").postTags("</span>");
		SearchResponse response = client.prepareSearch(indexName).setQuery(stringQuery).highlighter(highlightBuilder)
				.setSize(10000).get();
		SearchHits hits = response.getHits();
		Instant end = Instant.now();
		System.out.println("共搜索到: " + hits.getTotalHits() + " 条结果" + "," + "共耗时: "
				+ Duration.between(start, end).toMillis() + " 毫秒");
		List<Map<Object, Object>> list = QueryUtil.highlightQuery(hits);
		for (Map<Object, Object> map : list) {
			System.out.println(map.toString());
		}

		return list.toString();
	}

	/**
	 * 指定字段严格查询
	 * 
	 * @param indexName-索引名称
	 * @param field-查询字段
	 * @param keyword-关键字
	 * @return
	 */
	@RequestMapping("/term/{indexName}/{field}/{keyword}")
	public String highlightForTermQuery(@PathVariable String indexName, @PathVariable String field,
			@PathVariable String keyword) {
		// 获取当前时间
		Instant start = Instant.now();
		// 获取client对象
		Client client = elasticsearchTemplate.getClient();
		// 获取查询构造器
		QueryBuilder termQuery = QueryBuilders.termQuery(field, keyword);
		// 获取高亮构造器，设置字段、高亮的方式
		HighlightBuilder highlightBuilder = new HighlightBuilder().field(field)
				.preTags("<span style='color:red;font-weight:bold;'>").postTags("</span>");
		// client对象设置索引、查询器、高亮器、一次查询获取的量，返回SearchResponse对象
		SearchResponse response = client.prepareSearch(indexName).setQuery(termQuery).highlighter(highlightBuilder)
				.setSize(10000).get();
		// 通过SearchResponse对象获取SearchHits对象
		SearchHits hits = response.getHits();
		Instant end = Instant.now();
		System.out.println("共搜索到: " + hits.getTotalHits() + " 条结果" + "," + "共耗时: "
				+ Duration.between(start, end).toMillis() + " 毫秒");
		// 通过工具类获取返回封装的查询结果
		List<Map<Object, Object>> list = QueryUtil.highlightQuery(hits);
		for (Map<Object, Object> map : list) {
			System.out.println(map.toString());
		}
		return list.toString();
	}

	/**
	 * 选定字段合并查询
	 * 
	 * @param indexName-索引名称
	 * @param field-查询字段
	 * @param mustkeyword
	 * @return
	 */
	@RequestMapping("/bool/{indexName}/{mustfield}/{mustkeyword}/{mustnotfield}/{mustnotkeyword}")
	public String highlightForBoolQuery(@PathVariable String indexName, @PathVariable String mustfield,
			@PathVariable String mustkeyword, @PathVariable String mustnotfield, @PathVariable String mustnotkeyword) {
		Instant start = Instant.now();
		Client client = elasticsearchTemplate.getClient();
		QueryBuilder boolQuery = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery(mustfield, mustkeyword).operator(Operator.OR))
				.mustNot(QueryBuilders.termQuery(mustnotfield, mustnotkeyword));
		HighlightBuilder highlightBuilder = new HighlightBuilder().field(mustfield)
				.preTags("<span style='color:red;font-weight:bold;'>").postTags("</span>");
		// client对象设置索引、查询器、高亮器、一次查询获取的量，返回SearchResponse对象
		SearchResponse response = client.prepareSearch(indexName).setQuery(boolQuery).highlighter(highlightBuilder)
				.setSize(10000).get();
		// 通过SearchResponse对象获取SearchHits对象
		SearchHits hits = response.getHits();
		Instant end = Instant.now();
		System.out.println("共搜索到: " + hits.getTotalHits() + " 条结果" + "," + "共耗时: "
				+ Duration.between(start, end).toMillis() + " 毫秒");
		// 通过工具类获取返回封装的查询结果
		List<Map<Object, Object>> list = QueryUtil.highlightQuery(hits);
		for (Map<Object, Object> map : list) {
			System.out.println(map.toString());
		}
		return list.toString();
	}
}
