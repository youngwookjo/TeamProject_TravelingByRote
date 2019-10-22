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
	
	////이하 영욱씨 코드 카피
	public SearchHits searchByLoc(String index, String loc) throws IOException {
		System.out.println("* searchByLoc : " + index + " / loc : " + loc);
		SearchRequest searchRequest = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder
			.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("loc_type", loc + "여행")))
			.size(1000);
//			.sort("likes", SortOrder.DESC).sort("comments", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);
		return client.search(searchRequest, RequestOptions.DEFAULT).getHits();
	}
	
	public Object[] getFrequencyList(String index, String[] idArray) throws IOException {
		long count = countAll(index);
		System.out.println(index + " : " + count);
		TermVectorsRequest request = null;
		Map<String, Integer> map = new HashMap<String, Integer>();
		String key = null;
		int value = 0;
		for(String id : idArray) {
			request = new TermVectorsRequest(index, id);			
			request.setFields("text");
			request.setFieldStatistics(false); 
			request.setPositions(false); 
			request.setOffsets(false); 
			request.setPayloads(false);
			for(TermVector tv : client.termvectors(request, RequestOptions.DEFAULT).getTermVectorsList()) {
				for(Term term : tv.getTerms()) {
					key = term.getTerm();
					value = term.getTermFreq();
					if(map.containsKey(key)) {
						map.put(key, map.get(key) + value);
					} else {
						map.put(key, value);
					}
				}
			}
		}
		return map.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.filter(v -> v.getKey().length() > 1)
				.filter(v -> !(v.getKey().
						contains("추천") || v.getKey().contains("여행") || v.getKey().contains("스타")
						|| v.getKey().contains("그램") || v.getKey().contains("국내")))
				.limit(20)
				.toArray();
	}
	
}