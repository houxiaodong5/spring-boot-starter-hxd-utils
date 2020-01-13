package com.hxd.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class OSSUtil {

    @Autowired
    private OSSProperties ossProperties;


    //private static final String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    //private static final String accessKeyId = "***";
    //private static final String accessKeySecret = "***";

    public  ObjectListing getOSSOrigData(String bucketName,String prefix) {
        // 创建OSSClient实例。
        //OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        OSS ossClient =new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());

        // 构造ListObjectsRequest请求。
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        // 设置prefix参数来获取fun目录下的所有文件。
        listObjectsRequest.setPrefix(prefix);
        // 递归列出fun目录下的所有文件。
        ObjectListing listing = ossClient.listObjects(listObjectsRequest);
        ossClient.shutdown();
        return listing;
    }


    /**
     * 获取指定节点下某文件夹下OSS数据存储路径
     * @param bucketName 节点名称
     * @param prefix 该节点下文件夹，可以是多级目录，以/结尾
     * */
    public  List<String> getOSSData(String bucketName,String prefix) {
        List<String> list = new ArrayList<>();
        // 遍历所有文件。
        ObjectListing listing = getOSSOrigData(bucketName,prefix);
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            String url = "https://" + bucketName + "." + ossProperties.getEndpoint().split("//")[1] + "/" + key;
            //String name = key.substring(key.lastIndexOf("/") + 1);
            list.add(url);
        }
        return list;
    }



/***
     *      上传图片至OSS
     * @param file:用户上传至服务器的图片
     * @param bucketName:图片保存在OSS的bucket名称
     * @param saveFoldNode :图片保存在OSS的文件夹节点
     * @return String:返回图片在OSS中的全路径
     **/


    public  String updateImgToOSS(String bucketName,String saveFoldNode,MultipartFile file){
        try {
            File file1 = new File("D:\\oss-img_"+UUID.randomUUID().toString());
            if(!file1.exists()){
                file1.mkdir();
            }
            File fileout=new File(file1.getAbsolutePath()+"\\"+ UUID.randomUUID().toString()+".jpg");
            FileCopyUtils.copy(file.getBytes(),fileout);
            OSS ossClient =new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
            ossClient.putObject(bucketName, saveFoldNode+fileout.getName(),fileout);
            ossClient.shutdown();

            String realUrl="https://"+bucketName+"."+ossProperties.getEndpoint().split("//")[1]+"/"+saveFoldNode+fileout.getName();//图片真实路径
            fileout.delete();

            return realUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
