package com.zj.managesys.controller;

import com.zj.managesys.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${clubsys.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //这里的形参名不能乱填，得和前端表单的name属性一致（input type='file' name='file'）
        //这里的file是一个临时文件，临时存在某个位置，需要转存到指定位置，否则本次请求完成后临时文件就会删除
        log.info(file.toString());
        log.info(basePath);

        //原始文件名
        String originalFilename = file.getOriginalFilename();//xxx.jpg
        //获取原始文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载，这个方法不返回数据，因为要通过response对象直接向页面写回数据
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //通过输出流将文件写回到页面中，这样就能在浏览器中展示图片了
            ServletOutputStream servletOutputStream = response.getOutputStream();

            //设置响应头，表示响应数据类型为图片
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while( (len = fileInputStream.read(bytes)) != -1){
                servletOutputStream.write(bytes, 0, len);
                servletOutputStream.flush();
            }

            //关闭资源
            servletOutputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}