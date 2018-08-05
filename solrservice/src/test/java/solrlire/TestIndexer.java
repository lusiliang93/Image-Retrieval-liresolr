package solrlire;

import net.semanticmetadata.lire.solr.indexing.ParallelSolrIndexer;

import java.io.IOException;

public class TestIndexer {
    public static void main(String[] args) {
//        try {
//            Runtime.getRuntime().exec("java -jar " + "/home/liuhy/Work/solrservice/src/test/java/solrlire/ExecJar.jar");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String [] params = new String[] {"-i", "/home/liuhy/Work/indexer/infile.txt", "-o", "/home/liuhy/Work/indexer/outfile.xml", "-n", "8"};

        long startTime = System.currentTimeMillis();

        try {
            ParallelSolrIndexer.main(params);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }
}
