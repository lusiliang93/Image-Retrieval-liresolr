package com.pingan.servlet;

import com.pingan.utils.ImgUtil;
import com.pingan.utils.ServletUtil;
import com.pingan.utils.SolrUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ResourceBundle;

/* 图片查重接口
 * 模拟的时候uploadPath直接传下载url地址
 * 入参:
 * {
 *   "reportNo": "709843201234321",
 *   "documentInfo": [
 *       {
 *           "documentId": "2194382109",
 *           "businessKey": "81740921u5437590",
 *           "uploadPath": "flkdasjf23832rkjgdafs8"
 *       },
 *       {
 *           ...
 *       }
 *   ]
 * }
 *
 * 出参:
 * {
 *   "code": "200",
 *   "errMsg": "",
 *   "documentInfoList": [
 *      {
 *          "documentId": "90328742437",
 *          "businessKey": "93ufdasj98u21",
 *          "documentInfo": [   // 这个应该是要返回查到的所有重复图片，可能不止一张
 *              {
 *                  "repeatDocumentId": "83210943273214",
 *                  "businessKey": "4870312985432",
 *                  "originName": "ss.jpg"
 *              },
 *              {
 *                  ...
 *              }
 *          ]
 *      },
 *   ]
 * }
 * */

public class ImgRepeatServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json; charset=utf-8");
        ResourceBundle bundle = ResourceBundle.getBundle("config");

        String jsonString = ServletUtil.getJSONString(req, resp);

        // 服务器上存储图片的路径
        final String uploadPath = bundle.getString("uploadPath");

        String errMsg = "";

        // 解析http请求传来的参数
        JSONObject requestJSON = null;
        String reportNo = null;
        JSONArray documentInfo = null;

        // 待返回的documentInfoList参数，如果查到了图片有重复，把当前图片的信息和查到的重复图片信息返回
        // 如果所有查询的图片都不重复，documentInfoList 为空
        JSONArray documentInfoList = new JSONArray();
        try {
            requestJSON = new JSONObject(jsonString);
            reportNo = requestJSON.getString("reportNo");
            documentInfo = requestJSON.getJSONArray("documentInfo");

            for (int i = 0; i < documentInfo.length(); i++) {
                JSONObject document = documentInfo.getJSONObject(i);
                String documentId = document.getString("documentId");
                String businessKey = document.getString("businessKey");
                String jsonUploadPath = document.getString("uploadPath");
                // TODO: 这里也需要根据jsonUploadPath去查询图像的url
                String imageUrl = jsonUploadPath;

                // 根据图片地址下载图片到服务器的图片存储路径,图片根据documentId命名
                final String filename = uploadPath + ImgUtil.getUUID() + documentId + ".jpg";
                try {
                    ImgUtil.downloadImg(filename, imageUrl);

                    try {
                        // 下面发送http请求，请求solr服务器进行图片查重
                        final String baseUrl = bundle.getString("solrUrl");
                        final String solrUrl = baseUrl + "/lireq?loadImg=true&filename=" + filename;
                        // content是服务器返回的查询结构，可以访问如下地址查看：
                        // padl.paic.com.cm/solr/lire/lireq?loadImg=true&filename=/wls/solr/product/solr-img-upload/*.jpg
                        String content = SolrUtil.getQueryResult(solrUrl);

                        // repeatDocumentInfo是查出来的所有与当前图片重复的图片，是个数组
                        JSONArray repeatDocumentInfo = SolrUtil.getRepeatImgs(content);
                        if (null != repeatDocumentInfo) {
                            // repeatImgInfo是传过来查重的图片的信息以及查到的所有重复图片的信息
                            JSONObject repeatImgInfo = new JSONObject();
                            repeatImgInfo.put("documentId", documentId);
                            repeatImgInfo.put("businessKey", businessKey);
                            repeatImgInfo.put("documentInfo", repeatDocumentInfo);

                            documentInfoList.put(repeatImgInfo);
                        }

                        ImgUtil.deleteImg(filename);
                    } catch (Exception e) {
                        errMsg += "Error query from solr.\n";
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    errMsg += "Download image from the given url fail.\n";
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            errMsg += "Error parsing JSON parameters\n";
            e.printStackTrace();
        }

        int code;
        if (errMsg.equals(""))
            code = 200;
        else
            code = 201;

        JSONObject responseJSON = new JSONObject();
        responseJSON.put("errMsg", errMsg);
        responseJSON.put("code", code);
        responseJSON.put("documentInfoList", documentInfoList);

        out.println(responseJSON.toString());

        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletUtil.processGetRequest(req, resp);
    }
}