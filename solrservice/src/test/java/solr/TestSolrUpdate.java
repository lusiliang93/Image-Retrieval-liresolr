package solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TestSolrUpdate {
    SolrClient solrClient;

    public TestSolrUpdate() {
        final String solrUrl = "http://localhost:8983/solr/liresolr";
        solrClient = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    public void close() {
        try {
            solrClient.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /*
     * Solrj添加文档
     * */
    public void addDucument() throws IOException {

        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

        for (int i = 0; i < 10; i++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", i * 100);
            doc.addField("title", "title" + i);

            docs.add(doc);
            System.out.println(doc.toString());
        }
        try {

            UpdateResponse rsp = solrClient.add(docs);
            System.out.println("Add doc size" + docs.size() + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());

            UpdateResponse rspcommit = solrClient.commit();
            System.out.println("commit doc to index" + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());

        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    /*
    * 根据id删除文档
    * */
    public void deleteById(String id) {
        try {
            UpdateResponse rsp = solrClient.deleteById(id);
            solrClient.commit();
            System.out.println("delete id:" + id + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * 根据查询条件删除文档
    * */
    public void deleteByQuery(String query) {
        UpdateResponse rsp;
        try {
            UpdateRequest commit = new UpdateRequest();
            commit.deleteByQuery(query);
            commit.setCommitWithin(500);
            commit.process(solrClient);
            System.out.println("url:"+commit.getPath()+"\t xml:"+commit.getXML()+" method:"+commit.getMethod());
//            rsp = client.deleteByQuery(query);
//            client.commit();
//            System.out.println("delete query:" + queryCon + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * 更新文档
    * */
    public void updateDocuments(int id,String fieldName, Object fieldValue) {
        HashMap<String, Object> oper = new HashMap<String, Object>();
//        多值更新方法
//        List<String> mulitValues = new ArrayList<String>();
//        mulitValues.add(fieldName);
//        mulitValues.add((String)fieldValue);
        oper.put("set", fieldValue);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", id);
        doc.addField(fieldName, oper);
        try {
            UpdateResponse rsp = solrClient.add(doc);
            System.out.println("update doc id:" + id + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
            UpdateResponse rspCommit = solrClient.commit();
            System.out.println("commit doc to index" + " result:" + rspCommit.getStatus() + " Qtime:" + rspCommit.getQTime());

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

    }

    public void addXMLDocuments() {
        /*
        * <add>
          <doc>
          <field name="id">111</field>
          <field name="title">hello</field>
          </doc>
          </add>
        * */
        File xml = new File("/home/liuhy/Downloads/outfile.xml");
        ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update");
        try {
            req.addFile(xml, "application/xml");
            solrClient.request(req);
            solrClient.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//        new TestSolrUpdate().addDucument();
//        new TestSolrUpdate().deleteById("300");
//        new TestSolrUpdate().deleteByQuery("id: 222");
//        new TestSolrUpdate().updateDocuments(333, "title", "ace");
        new TestSolrUpdate().addXMLDocuments();
    }
}
