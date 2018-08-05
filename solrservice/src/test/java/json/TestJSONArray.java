package json;

import org.json.JSONArray;
import org.json.JSONObject;

public class TestJSONArray {
    public static void main(String[] args) {


        String jsonContent = "[" +
                "{'imageUrl': 'http://img4.duitang.com/uploads/blog/201403/01/20140301211549_hQHxA.jpeg', 'documentId': 'kfldjhaghewgk37247htj4q347fgdsaj', 'isRepeat': '0'}, " +
                "{'imageUrl': 'http://imgsrc.baidu.com/imgad/pic/item/0d338744ebf81a4cb91b80f5dc2a6059252da6e5.jpg', 'documentId': 'kfldjhaghewgk37247htj4q347fgdsaj', 'isRepeat': '0'}, " +
                "{'imageUrl': 'http://imgsrc.baidu.com/imgad/pic/item/267f9e2f07082838a722bf2eb399a9014c08f1d9.jpg', 'documentId': 'kfldjhaghewgk37247htj4q347fgdsaj', 'isRepeat': '0'}]";
        JSONArray jsonArray = new JSONArray(jsonContent);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String imageUrl = jsonObject.getString("imageUrl");
            String documentId = jsonObject.getString("documentId");
            int isRepeat = jsonObject.getInt("isRepeat");

            System.out.println("imageUrl: " + imageUrl);
            System.out.println("documentId: " + documentId);
            System.out.println("isRepeat: " + isRepeat);
            System.out.println();
        }

        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject jsonObject = jsonArray.getJSONObject(j);
            jsonObject.put("isRepeat", .5);
        }

        System.out.println(jsonArray.toString());
    }
}
