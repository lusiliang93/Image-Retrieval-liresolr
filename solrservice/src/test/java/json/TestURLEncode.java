package json;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TestURLEncode {
    public static void main(String[] args) {
        try {
            String jsonString = "[" +
                    "{\"imageUrl\": \"http://img4.duitang.com/uploads/blog/201403/01/20140301211549_hQHxA.jpeg\", \"documentId\": \"kfldjhaghewgk37247htj4q347fgdsaj\", \"isRepeat\": \"0\"}, " +
                    "{\"imageUrl\": \"http://imgsrc.baidu.com/imgad/pic/item/0d338744ebf81a4cb91b80f5dc2a6059252da6e5.jpg\", \"documentId\": \"kfldjhaghewgk37247htj4q347fgdsaj\", \"isRepeat\": \"0\"}, " +
                    "{\"imageUrl\": \"http://imgsrc.baidu.com/imgad/pic/item/267f9e2f07082838a722bf2eb399a9014c08f1d9.jpg\", \"documentId\": \"kfldjhaghewgk37247htj4q347fgdsaj\", \"isRepeat\": \"0\"}]";
            System.out.println(jsonString);

            jsonString =  URLEncoder.encode(jsonString, "utf-8");
            System.out.println(jsonString);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
