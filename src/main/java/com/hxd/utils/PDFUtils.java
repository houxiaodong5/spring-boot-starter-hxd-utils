package com.hxd.utils;



import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class PDFUtils {
/**
     *      截取Pdf文件的某一页
     * */

    public static String splitPdf(int pageNum, String source, String dest) {
        File indexFile = new File(source);
        File outFile = new File(dest);
        PDDocument document = null;
        try {
            document = PDDocument.load(indexFile);
            // document.getNumberOfPages();
            Splitter splitter = new Splitter();
            splitter.setStartPage(pageNum);
            splitter.setEndPage(pageNum);
            List<PDDocument> pages = splitter.split(document);
            ListIterator<PDDocument> iterator = pages.listIterator();
            while (iterator.hasNext()) {
                PDDocument pd = iterator.next();
                if (outFile.exists()) {
                    outFile.delete();
                }
                pd.save(outFile);
                pd.close();
                if (outFile.exists()) {
                    return outFile.getPath();
                }
            }
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

/**
     *   将pdf转为图片
     * */

    public static void pdfFileToImage(File pdffile,String targetPath){
        try {
            FileInputStream instream = new FileInputStream(pdffile);
            InputStream byteInputStream=null;
            try {
                PDDocument doc = PDDocument.load(instream);
                PDFRenderer renderer = new PDFRenderer(doc);
                int pageCount = doc.getNumberOfPages();
                if (pageCount > 0) {
                    BufferedImage image = renderer.renderImage(0, 4.0f);
                    image.flush();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    ImageOutputStream imOut;
                    imOut = ImageIO.createImageOutputStream(bs);
                    ImageIO.write(image, "png", imOut);
                    byteInputStream = new ByteArrayInputStream(bs.toByteArray());
                    byteInputStream.close();
                }
                doc.close();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            File uploadFile = new File(targetPath);
            FileOutputStream fops;
            fops = new FileOutputStream(uploadFile);
            fops.write(readInputStream(byteInputStream));
            fops.flush();
            fops.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    //C:\\Users\\sxtc\\Desktop\\test\\"+uuid+".pdf
    public static void getFileByUrl(String fileUrl,String saveFilePath){
        try {
            //new一个URL对象
            //URL url = new URL("https://shujucaiji.oss-cn-beijing.aliyuncs.com/wwwdefensegov/20190810/pdf/F12F3F9F615502EE427D5E95923BEA29.pdf");
            URL url = new URL(fileUrl);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = readInputStream(inStream);
            //new一个文件对象用来保存图片，默认保存当前工程根目录
            //String uuid= UUID.randomUUID().toString();
            //File pfdFile = new File("C:\\Users\\sxtc\\Desktop\\test\\"+uuid+".pdf");
            File pfdFile=new File(saveFilePath);
            //创建输出流
            FileOutputStream outStream = new FileOutputStream(pfdFile);
            //写入数据
            outStream.write(data);
            //关闭输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            UUID uuid = UUID.randomUUID();
            final String  parentFile="C:\\Users\\sxtc\\Desktop\\test\\";
            //将远程pdf文件下载到本地
            String fileSavePath=parentFile+"\\orig_pdf\\"+uuid+".pdf";
            File f1;
            if(!(f1=new File(fileSavePath)).getParentFile().exists()){
                System.out.println(f1.getAbsolutePath());
                f1.getParentFile().mkdirs();
            }
            getFileByUrl("https://shujucaiji.oss-cn-beijing.aliyuncs.com/w11gov/211810/pdf/F11.pdf",fileSavePath);
            //截取Pdf文件的首页
            String splitPdfPath=parentFile+"\\split_pdf\\"+uuid+".pdf";
            File f2;
            if(!(f2=new File(splitPdfPath)).getParentFile().exists()){
                f2.getParentFile().mkdirs();
            }
            splitPdf(1,fileSavePath,splitPdfPath);
            //将截取的pdf转为图片
            String splitPdfToImgPath=parentFile+"\\split_pdf_img\\"+uuid+".png";
            File f3;
            if(!(f3=new File(splitPdfToImgPath)).getParentFile().exists()){
                f3.getParentFile().mkdirs();
            }
            pdfFileToImage(new File(splitPdfPath),splitPdfToImgPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
