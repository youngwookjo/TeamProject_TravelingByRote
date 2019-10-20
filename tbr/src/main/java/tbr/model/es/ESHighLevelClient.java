package tbr.model.es;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tbr.model.dto.InstaPostDTO;

@Component
public class ESHighLevelClient {
	private static RestHighLevelClient client = null;
	
	@Value("${elasticsearch.host}")
	private String host;
	@Value("${elasticsearch.port}")
	private int port;
	
	public void connect() {
		client = new RestHighLevelClient(
			        RestClient.builder(
			                new HttpHost(host, port, "http")));
	}
	
	public void close() throws IOException {
		client.close();
	}
	
	// https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-document-bulk.html
	public boolean bulk(String index, List<InstaPostDTO> list) throws IOException {
		BulkRequest request = new BulkRequest();
		InstaPostDTO post = null;
		for(int i = 0; i < list.size(); i++) {
			post = list.get(i);
			request.add(new IndexRequest(index).id(i + 1 + "")
					.source("loc_type", post.getLocType(),
							"img", post.getImg(),
							"text", post.getText(),
							"likes", post.getLikes(),
							"comments", post.getComments()));
		}
		client.bulk(request, RequestOptions.DEFAULT);
		return true;
	}
	
	// https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-search.html
	public SearchHits search(String index, String kwd) throws IOException {
		System.out.println(index);
		System.out.println(kwd);
		SearchRequest searchRequest = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchSourceBuilder.query(QueryBuilders.matchQuery("text", kwd)); 
//		searchSourceBuilder.query(QueryBuilders.matchQuery("text", kwd)).size(2).sort("likes", SortOrder.DESC); 
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		return searchResponse.getHits();
	}
	
}