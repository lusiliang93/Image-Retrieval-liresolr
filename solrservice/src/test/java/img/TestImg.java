package img;


import org.apache.commons.imaging.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class TestImg {

    public static void main(String[] args) throws IOException, ImageReadException {
        test1();
    }

    public static void formatImg(String filePath, ImageFormat sourceFormat, ImageFormat targetFormat) {
        File sourceImg = new File(filePath);

    }


    public static void test1() throws IOException, ImageReadException {
        String basePath = "/home/liuhy/Downloads/";
        String filename = basePath + "test.tif";

        File imageFile = new File(filename);

        // 判断文件是否存在
        System.out.println(Imaging.hasImageFileExtension(imageFile));
        ImageInfo imageInfo = Imaging.getImageInfo(imageFile);  // 获得图片信息
        System.out.println(imageInfo.getFormatName());
        System.out.println(imageInfo.getMimeType());
        System.out.println(Imaging.getImageSize(imageFile));    // 获得图片尺寸
        System.out.println(Imaging.guessFormat(imageFile));

        ImageFormat format = ImageFormats.PCX;
        BufferedImage image = Imaging.getBufferedImage(imageFile);
        File outfile = new File("/home/liuhy/Downloads/testtest.pcx");
        Map optional_params = new Hashtable();

        try {
            Imaging.writeImage(image, outfile, format, optional_params);
        } catch (ImageWriteException e) {
            e.printStackTrace();
        }

    }
}
