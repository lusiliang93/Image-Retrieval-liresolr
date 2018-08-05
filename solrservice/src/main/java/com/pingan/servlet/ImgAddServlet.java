package com.pingan.servlet;

import com.pingan.utils.ImgUtil;
import com.pingan.utils.ServletUtil;
import com.pingan.utils.SolrUtil;
import net.semanticmetadata.lire.solr.indexing.ParallelSolrIndexer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/* 图片信息上传接口，接口地址: localhost:8080/cvg/app/imgAdd

模拟时，uploadPath指的是aa.jpg所在的url路径
入参：
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "73278536258",
            "businessKey": "5681654716",
            "originName": "aa.jpg",
            "uploadPath": "fdhguiehsjdsahgadjsf"
        },
        {
            ....
        }
    ]

}

拿到参数后需要做的几件事：
1.下载图片到某个缓存路径，图片以documentId命名，并把所有的文件名包含路径存到一个数组里，等所有图片下载完，
  将文件名写入infile.txt
2.使用ParallelSolrIndexer创建索引文件，保存到outfile.xml
3.解析这个xml，构建solr文档，进行添加操作

返回：
{
    "code": "200"
    "errMsg": ""
}

添加图片时，是用 documentId_businessKey_originName 命名的

* */
public class ImgAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json; charset=utf-8");
        ResourceBundle bundle = ResourceBundle.getBundle("config");

        String jsonString = ServletUtil.getJSONString(req, resp);

        // 错误信息
        String errMsg = "";

        // 服务器上存储图片的路径
        final String uploadPath = bundle.getString("uploadPath");

        final String infilePath = uploadPath + "infile.txt";
        final String outfilePath = uploadPath + "outfile.xml";

        // 解析json
        JSONObject requestJSON = null;
        String reportNo = null;
        JSONArray documentInfo = null;
        try {
            requestJSON = new JSONObject(jsonString);
            reportNo = requestJSON.getString("reportNo");
            documentInfo = requestJSON.getJSONArray("documentInfo");

            // 用于存储文件名的List
            List<String> imgPathList = new ArrayList<String>();

            // 下面是下载图片并保存所有图片路径的代码
            try {
                // 1.下载图片，图片以id命名，把下载后图片的完整路径存储到List中
                int imgCount = documentInfo.length();
                for (int i = 0; i < imgCount; i++) {
                    // 分别对传过来的每张图片进行处理
                    JSONObject document = null;
                    String documentId = null;
                    String bussinessKey = null;
                    String originName = null;
                    String jsonUploadPath = null;

                    String imgUrl = null;
                    try {
                        document = documentInfo.getJSONObject(i);
                        documentId = document.getString("documentId");
                        bussinessKey = document.getString("businessKey");
                        originName = document.getString("originName");
                        jsonUploadPath = document.getString("uploadPath");

                        // imgUrl = ImgUtil.getUrl(jsonUploadPath);
                        // TODO: 这个最后要修改，url是查出来的
                        imgUrl = document.getString("uploadPath") + "/" + originName;
                        String filename = uploadPath + documentId + "_"
                                + bussinessKey + "_" + originName;
                        try {
                            ImgUtil.downloadImg(filename, imgUrl);
                            imgPathList.add(filename);
                        } catch (Exception e) {
                            errMsg += "Fail download image from: " + imgUrl + "\n";
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        errMsg += "Error parsing JSON documentInfo\n";
                        e.printStackTrace();
                    }
                }

                // 2.创建infile.txt，把那个保存文件名的List存起来，这个文件同样存储在uploadPath路径下
                File infile = new File(infilePath);
                if (infile.exists())
                    infile.delete();
                infile.createNewFile();
                FileWriter fw = new FileWriter(infile);
                BufferedWriter bw = new BufferedWriter(fw);
                for (int i = 0; i < imgPathList.size(); i++) {
                    bw.write(imgPathList.get(i) + "\n");
                }
                bw.close();
                fw.close();

                // 3.使用liresolr进行特征提取后，生成的文档同样存储在uploadPath路径下，特征提取之前，先检查
                //   这个路径下有没有outfile.xml这个文件，如果有，将其删除
                File outfile = new File(outfilePath);
                if (outfile.exists())
                    outfile.delete();

                // 4.提取特征，生成文档
                try {
                    String[] params = new String[]{"-i", infilePath, "-o", outfilePath};
                    ParallelSolrIndexer.main(params);
                } catch (Exception e) {
                    errMsg += "Error generate index docs\n";
                    e.printStackTrace();
                }

                // 5.文档生成完之后，下载下来的图片就没用了，将其删除
                for (String filename : imgPathList) {
                    File file = new File(filename);
                    file.delete();
                }
            } catch (Exception e) {
                errMsg += "Error extract features\n";
                e.printStackTrace();
            }
        } catch (Exception e) {
            errMsg += "Error parsing JSON parameters\n";
            e.printStackTrace();
        }

        // 将生成的 outfile.xml 添加到solr服务器
        if (errMsg.equals("")) {
            // 将生成的xml提交到solr服务器
            String solrUrl = bundle.getString("solrUrl");
            try {
                SolrUtil.addXMLDocuments(solrUrl, outfilePath);
            } catch (Exception e) {
                errMsg += "Error post index file\n";
                e.printStackTrace();
            }
        }

        String jsonResult = ServletUtil.generateJSONResult(errMsg);
        out.println(jsonResult);
        out.flush();
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletUtil.processGetRequest(request, response);
    }
}
