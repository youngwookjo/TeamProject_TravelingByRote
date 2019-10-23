package tbr.model.es;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse.TermVector;
import org.elasticsearch.client.core.TermVectorsResponse.TermVector.Term;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tbr.model.dto.InstaPostDTO;
import tbr.model.dto.TagDTO;

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
	
	public boolean deleteIndex(String index) throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest(index);
		client.indices().delete(request, RequestOptions.DEFAULT);
		return true;
	}
	
	public boolean createIndex(String index, Map<String, Object> source) throws IOException {
		// https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-create-index.html
		CreateIndexRequest request = new CreateIndexRequest(index);
		request.source(source);
		client.indices().create(request, RequestOptions.DEFAULT);
		return true;
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
	public long countAll(String index) throws IOException {
		System.out.println("* countAll : " + index);
		CountRequest request = new CountRequest(index);
		SearchSourceBuilder builder = new SearchSourceBuilder(); 
		builder.query(QueryBuilders.matchAllQuery()); 
		request.source(builder);
		CountResponse response = client
			    .count(request, RequestOptions.DEFAULT);
		return response.getCount();
	}
	
	public SearchHits searchByKwd(String index, String kwd) throws IOException {
		System.out.println("* searchByKwd : " + index + " / kwd : " + kwd);
		SearchRequest searchRequest = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder
			.query(QueryBuilders.matchQuery("text", kwd))
			.size(1000);
//			.sort("likes", SortOrder.DESC).sort("comments", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);
		return client.search(searchRequest, RequestOptions.DEFAULT).getHits();
	}

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
	
	public SearchHits searchByLocAndKwd(String index, String loc, String kwd) throws IOException {
		System.out.println(index + " / kwd : " + kwd + " loc : " + loc);
		SearchRequest searchRequest = new SearchRequest(index);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder
			.query(QueryBuilders.boolQuery()
				.filter(QueryBuilders.termQuery("loc_type", loc + "여행"))
				.must(QueryBuilders.matchQuery("text", kwd)))
			.size(1000);
//			.sort("likes", SortOrder.DESC).sort("comments", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);
		return client.search(searchRequest, RequestOptions.DEFAULT).getHits();
	}
	
	public List<TagDTO> getAllFrequencyList(String index) throws IOException {
		long count = countAll(index);
		System.out.println(index + " : " + count);
		TermVectorsRequest request = null;
		Map<String, Integer> map = new HashMap<String, Integer>();
		String key = null;
		int value = 0;
		for(int i = 1; i <= count; i++) {
			request = new TermVectorsRequest(index, i+"");			
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
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.filter(v -> v.getKey().length() > 1)
				.filter(v -> !(v.getKey().
						contains("추천") || v.getKey().contains("여행") || v.getKey().contains("스타")
						|| v.getKey().contains("그램") || v.getKey().contains("국내")))
				.filter(v -> {
					try {
						// https://www.urlencoder.io/java/
						// http://docs.oracle.com/cd/E24693_01/server.11203/e10729/appunicode.htm#CACHBDGH
						return !URLEncoder.encode(v.getKey(), StandardCharsets.UTF_8.toString())
								.substring(1, 2).startsWith("F");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return true;
					}})
				.limit(20).map(v -> new TagDTO(v.getKey(), v.getValue())).collect(Collectors.toList());
	}
	
	public List<TagDTO> getFrequencyList(String index, String[] idArray) throws IOException {
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
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.filter(v -> v.getKey().length() > 1)
				.filter(v -> !(v.getKey().
						contains("추천") || v.getKey().contains("여행") || v.getKey().contains("스타")
						|| v.getKey().contains("그램") || v.getKey().contains("국내")))
				.filter(v -> {
					try {
						return !URLEncoder.encode(v.getKey(), StandardCharsets.UTF_8.toString())
								.substring(1, 2).startsWith("F");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return true;
					}})
				.limit(20).map(v -> new TagDTO(v.getKey(), v.getValue())).collect(Collectors.toList());
	}
	
	public List<TagDTO> getFrequencyList(String index, String[] idArray, String[] words) throws IOException {
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
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.filter(v -> v.getKey().length() > 1)
				.filter(v -> !(v.getKey().
						contains("추천") || v.getKey().contains("여행") || v.getKey().contains("스타")
						|| v.getKey().contains("그램") || v.getKey().contains("국내")))
				.filter(v -> !Arrays.asList(words).contains(v.getKey()))
				.filter(v -> {
					try {
						return !URLEncoder.encode(v.getKey(), StandardCharsets.UTF_8.toString())
								.substring(1, 2).startsWith("F");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return true;
					}})
				.limit(20).map(v -> new TagDTO(v.getKey(), v.getValue())).collect(Collectors.toList());
	}
}