package com.sky.utils;

import cn.hutool.crypto.digest.BCrypt;
import com.sky.properties.BcryptProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BcryptUtil {

    private final BcryptProperties bcrypt;

    /**
     * 对密码进行加密，根据配置盐值加密
     * @param password 密码
     * @return .
     */
    public String encode(String password) {
        return BCrypt.hashpw(
                password,
                BCrypt.gensalt(
                        Integer.parseInt(
                                bcrypt.getSalt()
                        )
                )
        );
    }

    /**
     * 验证密码是否匹配
     * @param password 需要比对的密码
     * @param encodedPassword 加密后的密码
     * @return .
     */
    public boolean matches(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }

}
