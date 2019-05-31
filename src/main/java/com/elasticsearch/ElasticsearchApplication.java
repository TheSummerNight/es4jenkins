package com.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

@SpringBootApplication
public class ElasticsearchApplication {
	@Autowired
	public static ElasticsearchTemplate elasticsearchTemplate;

	public static void main(String[] args) {

		SpringApplication.run(ElasticsearchApplication.class, args);
	}

}
