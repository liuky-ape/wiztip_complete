package com.wiztip.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.wiztip.config.WiztipProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 阿里云OSS对象存储服务
 * 
 * 提供文件上传到阿里云OSS的功能
 * 支持音频文件的持久化存储和URL访问
 * 
 * @author Wiztip Team
 */
@Service
public class OssService {

    @Autowired
    private WiztipProperties wiztipProperties;

    /**
     * 上传文件到阿里云OSS
     * 
     * 文件按用户ID分目录存储，文件名添加时间戳避免重复
     * 存储路径格式：user_{userId}/{timestamp}_{原始文件名}
     * 
     * @param file 待上传的文件
     * @param userId 用户ID，用于文件路径分类
     * @return 上传后的文件访问URL
     * @throws IOException 文件读取或上传失败时抛出异常
     */
    public String upload(MultipartFile file, String userId) throws IOException {
        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(
            wiztipProperties.getAliyun().getOss().getEndpoint(),
            wiztipProperties.getAliyun().getOss().getAccessKeyId(),
            wiztipProperties.getAliyun().getOss().getAccessKeySecret()
        );
        
        // 构造对象名称：按用户分目录，添加时间戳避免重名
        String objectName = "user_" + userId + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        
        // 上传文件到OSS
        ossClient.putObject(wiztipProperties.getAliyun().getOss().getBucket(), objectName, file.getInputStream());
        
        // 关闭客户端
        ossClient.shutdown();
        
        // 返回文件的完整访问URL
        return "https://" + wiztipProperties.getAliyun().getOss().getBucket() + "." + 
               wiztipProperties.getAliyun().getOss().getEndpoint() + "/" + objectName;
    }
}
