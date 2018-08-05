package com.pingan.utils;

import com.sun.deploy.util.ArrayUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.PortableInterceptor.INACTIVE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SolrUtil {

    /*
     * 根据传来的url请求solr服务器，并返回http响应中的json数据部分
     * */
    public static String getQueryResult(String url) throws ExecutionException, InterruptedException, IOException {
        // 下面发送http请求，请求solr服务器进行图片查重
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        final CountDownLatch latch = new CountDownLatch(1);
        final HttpGet request = new HttpGet(url);
        Future<HttpResponse> future = httpclient.execute(request, null);
        String result = null;
        HttpResponse response = future.get();
        result = EntityUtils.toString(response.getEntity(), "UTF-8");
        httpclient.close();

        return result;
    }

    /*
     * 根据json数据判断图片是否重复
     * */
    @Deprecated
    public static int isRepeat(String content) {
        float threshold = 0.95f;

        JSONObject response = new JSONObject(content);
        JSONArray docs = response.getJSONArray("docs");
//        List<Float> scores = new ArrayList<Float>();
//        for (int i = 0; i < docs.length(); i++) {
//            JSONObject doc = docs.getJSONObject(i);
//            scores.add(doc.getFloat("d"));
//        }
        JSONObject firstDoc = docs.getJSONObject(0);
        float maxScore = firstDoc.getFloat("d");

        return maxScore > threshold ? 1 : 0;
    }

    /*
     * 根据json数据判断图片是否重复，如果重复，把重复的图片信息构造成一个JSONArray返回，
     * 如果不重复，直接返回null
     * */
    public static JSONArray getRepeatImgs(String content) {
        float threshold = 0.95f;
        JSONArray documentInfo = new JSONArray();
        JSONObject response = new JSONObject(content);
        JSONArray docs = response.getJSONArray("docs");

        // 返回的结果是排好序的，降序排列
        float maxScore = docs.getJSONObject(0).getFloat("d");
        if (maxScore < threshold)   // 最大的得分小于阈值，说明没有重复的图片
            return null;
        else {
            for (int i = 0; i < docs.length(); i++) {
                JSONObject doc = docs.getJSONObject(i);
                float score = doc.getFloat("d");
                String id = doc.getString("id");
                if (score < maxScore)
                    break;

                JSONObject imgInfo = new JSONObject();
                String[] fields = id.split("/");
                fields = fields[fields.length - 1].split("_");
                imgInfo.put("repeatDocumentId",fields[0]);
                imgInfo.put("businessKey", fields[1]);
                imgInfo.put("originName", fields[2]);
                documentInfo.put(imgInfo);
            }
        }

        return documentInfo;
    }

    /*
     * 根据索引的id删除索引
     * */
    public static boolean deleteIndexById(final String solrUrl, String id) throws IOException, SolrServerException {
        boolean result = false;
        SolrClient solrclient = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        UpdateResponse rsp = solrclient.deleteById(id);
        solrclient.commit();
        System.out.println("delete id:" + id + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
        result = true;
        try {
            solrclient.close();
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    /*
     * solrUrl: solr collection地址
     * xmlFilePath: 要上传的xml文件路径
     * */
    public static void addXMLDocuments(String solrUrl, String xmlFilePath) throws IOException, SolrServerException {
        File xml = new File(xmlFilePath);
        if (!xml.exists())
            throw new FileNotFoundException("xml file doesn't exists");

        SolrClient solrClient = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update");
        req.addFile(xml, "application/xml");
        solrClient.request(req);
        solrClient.commit();
        solrClient.close();
    }

    public static void main(String[] args) {
        String url = "/wls/product/solr/img-upload/documentId_businessKey_originName";
        String[] fields = url.split("/");
        fields = fields[fields.length - 1].split("_");
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }
    }


}
