package com.pingan.utils;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ResourceBundle;

public class ServletUtil {
    public static void processGetRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        out.println("Only process post request");

        out.flush();
        out.close();
    }

    public static String getJSONString(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 拿出URL中的json字符串，并将它转成JSONArray对象
        StringBuffer sb = new StringBuffer();
        InputStream is = req.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String s = "";
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        String jsonString = sb.toString();
        jsonString.replaceAll("\\\\", "");

        return jsonString;
    }

    public static String generateJSONResult(String errMsg) {
        String code = "200";
        if (!errMsg.equals(""))
            code = "201";
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("errMsg", errMsg);

        return result.toString();
    }


}
