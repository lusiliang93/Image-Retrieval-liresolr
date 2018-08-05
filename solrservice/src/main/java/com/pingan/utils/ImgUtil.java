package com.pingan.utils;

import org.apache.commons.imaging.*;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ImgUtil {

    /*
     * 根据url下载图片到指定路径
     * */
    public static void downloadImg(String savePath, String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        byte[] buffer = new byte[4096];
        OutputStream os = new FileOutputStream(savePath);
        int len;

        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        os.close();
        is.close();
    }

    /*
     * 图像查重接口中，要先把图片下载下来再查重
     * */
    public static void deleteImg(String filename) {
        File img = new File(filename);
        img.delete();
    }

    /*
     * 用于清空保存图片的文件夹
     * */
    public static void clearDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.length() == 0)
            return;

        File[] files = dir.listFiles();
        if (files.length == 0)
            return;

        for (File file : files)
            file.delete();
    }

    /*
     * 生成UUID，用作图片文件名
     * */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /*
     * 判断一张图片是RGB还是CMYK
     * */
    public static boolean isRGB(String filename) throws IOException {
        File file = new File(filename);
        boolean isRgb = true;

        //创建输入流
        ImageInputStream input = ImageIO.createImageInputStream(file);
        Iterator readers = ImageIO.getImageReaders(input);
        if (readers == null || !readers.hasNext()) {
            throw new RuntimeException("No ImageReaders found");
        }

        ImageReader reader = (ImageReader) readers.next();
        reader.setInput(input);
        //获取文件格式
        BufferedImage image;
        try {
            // 尝试读取图片 (包括颜色的转换).
            reader.read(0); // RGB
            isRgb = true;
        }
        catch (IIOException e) {
            // 读取Raster (没有颜色的转换).
            reader.readRaster(0, null);// CMYK
            isRgb = false;
        }
        return isRgb;
    }

    /*
     * 传入源图片的路径, 要转换的图片的路径, 要转换成的图片的格式(一般为png), 进行图片格式转换
     * */
    public static void formatImg(String sourceImgPath, String targetImgPath, ImageFormat targetFormat) throws IOException, ImageReadException, ImageWriteException {
        File sourceImg = new File(sourceImgPath);
        File targetImg = new File(targetImgPath);

        BufferedImage image = Imaging.getBufferedImage(sourceImg);
        Map optional_params = new Hashtable();
        Imaging.writeImage(image, targetImg, targetFormat, optional_params);
    }

    public static void png2Jpg(String sourceImgPath, String targetImgPath) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(sourceImgPath));
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

            ImageIO.write(newBufferedImage, "jpg", new File(targetImgPath));

            System.out.println("Done");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 传入String形式的图片扩展名, 如果该扩展名可以处理, 就返回PNG, 否则返回UNKNOWN
     * */
    public static ImageFormat getImageFormat(String extension) {
        ImageFormat targetFormat = null;
        if (null == extension || extension.equals(""))
            targetFormat = ImageFormats.UNKNOWN;

        extension = extension.toLowerCase();
        String[] tables = new String[]{"jpg", "jpeg", "bmp", "gif", "tif", "tiff", "png", "psd", "pcx", "jfif"};
        for (String s : tables) {
            if (s.equals(extension)) {
                targetFormat = ImageFormats.PNG;
                break;
            }
        }

        return targetFormat;
    }


    public static void main(String[] args) throws Exception {
//        try {
//            System.out.println(isRGB("/home/liuhy/Downloads/test.jpg"));
//            System.out.println(isRGB("/home/liuhy/Downloads/gan.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        ImgUtil.cmyk2rgb("/home/liuhy/Downloads/test.jpg");

//        String[] params = new String[]{"-i", "/home/liuhy/Downloads/infile.txt", "-o", "/home/liuhy/Downloads/outfile.xml", "-f"};
//        ParallelSolrIndexer.main(params);

//        String[] ext = new String[] {"jpg", "bmp"};
//         String[] tables = new String[]{"jpg", "jpeg", "bmp", "gif", "tif", "tiff", "png", "psd", "pcx", "jfif"};
//         for (String s : tables) {
//             System.out.println(s);
//         }

        String srcImgPath = "/home/liuhy/Downloads/test.png";
        String tarImgPath = "/home/liuhy/Downloads/m.dib";
        System.out.println(Imaging.guessFormat(new File(tarImgPath)));
        // Imaging.guessFormat(new File(srcImgPath));
        // png2Jpg(srcImgPath, tarImgPath);

    }

}
