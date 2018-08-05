package com.pingan.servlet;

import com.pingan.utils.ServletUtil;
import com.pingan.utils.SolrUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ResourceBundle;


/* 删除图像接口:localhost:8080/cvg/app/imgDelete

入参：
{
    "reportNo": "0940385947897386547",
    "documentInfo": [
        {
            "businessKey": "583029574392",
            "documentId": "823812077428174",
            "originName": "aa.jpg"
         },
    ]
}

出参：
{
    "code": "200",
    "errMsg": ""
}

* */

public class ImgDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json; charset=utf-8");
        ResourceBundle bundle = ResourceBundle.getBundle("config");

        String jsonString = ServletUtil.getJSONString(req, resp);

        // 错误信息
        String errMsg = "";

        // 解析json
        JSONObject requestJSON = null;
        String reportNo = null;
        JSONArray documentInfo = null;
        try {
            requestJSON = new JSONObject(jsonString);
            reportNo = requestJSON.getString("reportNo");
            documentInfo = requestJSON.getJSONArray("documentInfo");

            final String solrUrl = bundle.getString("solrUrl");
            for (int i = 0; i < documentInfo.length(); i++) {
                JSONObject document = null;
                String businessKey = null;
                String documentId = null;
                String originName = null;
                try {
                    document = documentInfo.getJSONObject(i);
                    businessKey = document.getString("businessKey");
                    documentId = document.getString("documentId");
                    originName = document.getString("originName");
                    // 添加图片时，是用 documentId_businessKey_originName 命名的
                    // 添加索引时，是根据 path/to/img/documentId_businessKey_originName命名的，这里的path就是uploadPath
                    final String uploadPath = bundle.getString("uploadPath");
                    // 图片添加时的命名格式: uploadPath/documentId_businessKey_originName
                    String deleteId = uploadPath + documentId + "_"
                            + businessKey + "_" + originName;
                    try {
                        SolrUtil.deleteIndexById(solrUrl, deleteId);
                    } catch (SolrServerException e) {
                        errMsg += "Error delete image, documentId: " + documentId;
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    errMsg += "Error parsing JSON documentInfo\n";
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            errMsg += "Error parsing JSON parameters\n";
            e.printStackTrace();
        }

        // 构造要返回的json参数
        String jsonResponse = ServletUtil.generateJSONResult(errMsg);
        out.println(jsonResponse);
        out.flush();
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletUtil.processGetRequest(req, resp);
    }
}