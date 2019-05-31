package com.elasticsearch.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

@Component
public class QueryUtil {
	public static List<Map<Object, Object>> highlightQuery(SearchHits hits) {
		List<Map<Object, Object>> list = new ArrayList();
		for (int i = 0; i < hits.getTotalHits(); i++) {
			SearchHit hit = hits.getAt(i);
			Map<Object, Object> map = new LinkedHashMap<>();
			map.put("Source As String", hit.getSourceAsString());
			map.put("score", hit.getScore());
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			for (String field : highlightFields.keySet()) {
				Text[] text = highlightFields.get(field).getFragments();
				String highlight = "";
				if (text != null) {
					for (Text str : text) {
						highlight += str;
					}
				}
				map.put("Highlight", highlight);
				map.put("highlightfield", field);
				list.add(map);
			}
		}
		return list;
	}
	
	public static List<String> getIndexFieldList(String fieldName, Map<String, Object> mapProperties) {
		List<String> fieldList = new ArrayList<String>();
		Map<String, Object> map = (Map<String, Object>) mapProperties.get("properties");
		Set<String> keys = map.keySet();
		for (String key : keys) {
			if (((Map<String, Object>) map.get(key)).containsKey("type")) {
				fieldList.add(fieldName + "" + key);
			} else {
				List<String> tempList = getIndexFieldList(fieldName + "" + key + ".",
						(Map<String, Object>) map.get(key));
				fieldList.addAll(tempList);
			}
		}
		return fieldList;
	}
}
