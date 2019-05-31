package com.elasticsearch.controller;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	/**
	 * 添加索引
	 * 
	 * @param indexName-索引名称
	 * @return
	 */
	@RequestMapping("/addIndex/{indexName}")
	public String addIndex(@PathVariable String indexName) {
		try {
			elasticsearchTemplate.createIndex(indexName);
			return "AddIndex SUCCESS~~~";
		} catch (Exception e) {
			return e.getMessage().toString();
		}
	}

	/**
	 * 更新索引
	 * 
	 * @param indexName-索引名称
	 * @param type-类型
	 * @param mapping-设置映射
	 * @return
	 */
	@PostMapping("/updateIndex/{indexName}/{type}")
	public String updateIndex(@PathVariable String indexName, @PathVariable String type, @RequestBody Map mapping) {
		try {
			elasticsearchTemplate.putMapping(indexName, type, mapping);
			return "UpdateIndex SUCCESS~~~";
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return e.getMessage().toString();
		}
	}

	/**
	 * 列出所有索引
	 * 
	 * @return
	 */
	@RequestMapping("/listIndex")
	public Set listIndex() {
		Client client = elasticsearchTemplate.getClient();
		IndicesAdminClient indices = client.admin().indices();
		ActionFuture<IndicesStatsResponse> isr = indices.stats(new IndicesStatsRequest().all());
		Set<String> indexSet = isr.actionGet().getIndices().keySet();
		return indexSet;
	}

	/**
	 * 删除索引
	 * 
	 * @param indexName-索引名称
	 * @return
	 */
	@RequestMapping("/deleteIndex/{indexName}")
	public String deleteIndex(@PathVariable String indexName) {
		try {
			elasticsearchTemplate.deleteIndex(indexName);
			return "DeleteIndex SUCCESS~~~";
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			return e.getMessage().toString();
		}
	}

	/**
	 * 获取索引mapping
	 * 
	 * @param indexName-索引名称
	 * @param type-类型
	 * @return
	 */
	@RequestMapping("/getIndexMapping/{indexName}/{type}")
	public Map getIndexMapping(@PathVariable String indexName, @PathVariable String type) {
		Map mapping = elasticsearchTemplate.getMapping(indexName, type);
		// System.out.println(mapping);
		return mapping;
	}

	@RequestMapping("/getIndexSetting/{indexName}")
	public Map getIndexSetting(@PathVariable String indexName) {
		Map mapping = elasticsearchTemplate.getSetting(indexName);
		// System.out.println(mapping);
		return mapping;
	}

}
