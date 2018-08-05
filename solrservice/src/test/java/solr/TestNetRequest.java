package solr;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestNetRequest {
    public static void main(String[] argv) {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        final HttpGet request = new HttpGet("http://localhost:8983/solr/liresolr/select?q=*:*");

        /*httpclient.execute(request, new FutureCallback<HttpResponse>() {

            public void completed(final HttpResponse response) {
                System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
                try {
                    String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                    System.out.println(" response content is : " + content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void failed(final Exception ex) {
                System.out.println(request.getRequestLine() + "->" + ex);
            }

            public void cancelled() {
                System.out.println(request.getRequestLine() + " cancelled");
            }

        });
*/
        Future<HttpResponse> future = httpclient.execute(request, null);
        try {
            HttpResponse response = future.get();
            String content = null;
            try {
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(" response content is : " + content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            httpclient.close();
        } catch (IOException ignore) {

        }
    }
}
