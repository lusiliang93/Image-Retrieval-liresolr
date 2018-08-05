package file;

import java.io.File;

public class TestFileAPI {

    public static void main(String []args) {
        String path = "/home/liuhy/Work/solrservice/target/solr-service/downloads";
        File dir = new File(path);
//        System.out.println(dir.length());

        File[] files = dir.listFiles();
        System.out.println(files.length);
//        for (File file: files)
//            file.delete();
    }
}
