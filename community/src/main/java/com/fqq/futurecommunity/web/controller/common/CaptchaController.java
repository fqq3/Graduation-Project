package com.fqq.futurecommunity.web.controller.common;
import com.fqq.futurecommunity.common.utils.RedisCache;
import com.fqq.futurecommunity.common.constant.Constants;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.UUIDUtils;
import com.wf.captcha.SpecCaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author: fqq
 * @Date: 2026/4/9 - 04 - 09 - 22:34
 * @Description: com.fqq.futurecommunity.web.controller.common
 * @version: 1.0
 * 获取验证码
 */
@RestController
public class CaptchaController {

    @Resource
    private RedisCache redisCache;

    @GetMapping("/captchaImage")
    public ChainedMap getCode(HttpServletResponse response){
        // 生成图片验证码（宽高长）
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);

        // UUID
        String uuid = UUIDUtils.simpleUUID();
        String key = Constants.CAPTCHA_CODE_KEY + uuid;

        // 验证码
        String code = specCaptcha.text().toLowerCase();

        // 存 Redis（使用工具类）
        redisCache.setCacheObject(
                key,
                code,
                Constants.CAPTCHA_EXPIRATION,
                TimeUnit.MINUTES
        );

        return ChainedMap.create()
                .set("uuid", uuid)
                .set("img", specCaptcha.toBase64());

    }
}
