package solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;

import java.util.HashMap;
import java.util.Map;

public class TestSolrQuery {

    public static void test1() {
        // build solr client
        final String solrUrl = "http://localhost:8983/solr";
        HttpSolrClient client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        // construct query parameters
        final Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("q", "*:*");
        queryParamMap.put("fl", "id, title");
        queryParamMap.put("sort", "id asc");
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);

        // query and parse result
        final QueryResponse response;
        try {
            response = client.query("liresolr", queryParams);
            final SolrDocumentList documents = response.getResults();

            print("Found " + documents.getNumFound() + " documents");
            for (SolrDocument document : documents) {
                final String id = (String) document.getFirstValue("id");
                final String title = (String) document.getFirstValue("title");
                print("id: " + id + "; title: " + title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test2() {
        // build solr client
        final String solrUrl = "http://localhost:8983/solr/liresolr";
        HttpSolrClient client = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        // construct query parameters
        final SolrQuery solrQuery = new SolrQuery("*:*");
//        solrQuery.addField("id");
//        solrQuery.addField("title");'
        solrQuery.setRequestHandler("/select");
        solrQuery.set("fl", "id, title");
        solrQuery.setSort("id", SolrQuery.ORDER.asc);
//        solrQuery.setRows(3);
        solrQuery.setShowDebugInfo(true);


        // 看看请求发的对不对
        print("RequestHandler: ");
        print(solrQuery.getRequestHandler());
        print("Query: ");
        print(solrQuery.getQuery());
        solrQuery.setShowDebugInfo(true);

        // query and parse result
        final QueryResponse response;
        try {
            response = client.query(solrQuery);
            print(response.getResults().toString());
            final SolrDocumentList documents = response.getResults();

            print("Found " + documents.getNumFound() + " documents");
            for (SolrDocument document : documents) {
                final String id = (String) document.getFirstValue("id");
                final String title = (String) document.getFirstValue("title");
                print("id: " + id + "; title: " + title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        test1();
        test2();
//        String a = null;
//        print(a.toString());
    }

    private static void print(String s) {
        System.out.println(s);
    }
}
