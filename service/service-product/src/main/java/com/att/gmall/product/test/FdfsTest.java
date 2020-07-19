package com.att.gmall.product.test;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;

public class FdfsTest {
    public static void main(String[] args) throws IOException, MyException {
        //读取配置
        String path = FdfsTest.class.getClassLoader().getResource("tracker.conf").getPath();
        System.out.println(path);

        //创建tracker连接
        ClientGlobal.init(path);

        TrackerClient trackerClient=new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();

        //获得storage连接
        StorageClient storageClient=new StorageClient(connection,null);
            //通过storage上传文件
        String[] jpgs = storageClient.upload_file("f:/a.jpg", "jpg", null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }
}
