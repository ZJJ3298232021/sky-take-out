package com.sky.controller.admin;

import com.sky.constant.DateFormatConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PathConstant;
import com.sky.context.BaseContext;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import com.sky.utils.DateStringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 *   @description: 通用接口
 */
@RestController
@Slf4j
@Tag(name = "通用接口")
@RequestMapping(PathConstant.ADMIN_COMMON)
@RequiredArgsConstructor
public class CommonController {
    private final AliOssUtil ossUtil;

    /*
     * 文件上传
     */
    @PostMapping("/upload")
    @Operation(description = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传，文件名为：{}", file);
        try {

            //示例：14-20230907101413-fuzz.jpg
            String objectName =
                    "img/" + BaseContext.getCurrentId() + "-" +
                            DateStringUtil
                                    .getNowDateString(
                                            DateFormatConstant.DATE_FORMAT
                                    )
                            + "-" + file.getOriginalFilename();
            String url = ossUtil.upload(file.getBytes(), objectName);
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
