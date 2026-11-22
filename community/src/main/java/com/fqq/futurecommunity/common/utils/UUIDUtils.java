package com.fqq.futurecommunity.common.utils;

import java.util.UUID;

/**
 * @author: fqq
 * @Date: 2026/4/9 - 04 - 09 - 20:32
 * @Description: com.fqq.futurecommunity.common.utils
 * @version: 1.0
 */
public class UUIDUtils {

        /**
         * @return 随机UUID
         */
        public static String randomUUID()
        {
            return UUID.randomUUID().toString();
        }

        /**
         * @return 简化的UUID，去掉了横线
         */
        public static String simpleUUID()
        {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }

}
