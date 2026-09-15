package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

//通用的接口，用于处理一些通用的业务逻辑
@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传");
        //获取原始文件名
        String fileName = file.getOriginalFilename();
        //截取后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //这样随机生成+后缀保证文件名唯一
        String objectName = UUID.randomUUID().toString()+suffix;
        String filePath = aliOssUtil.upload(file.getBytes(), objectName);
        return Result.success(filePath);   //文件路径传回去前端去访问那个网址即可显示图片 并且之后提交菜品的时候还能把图片路径一起传过来
    }
}
