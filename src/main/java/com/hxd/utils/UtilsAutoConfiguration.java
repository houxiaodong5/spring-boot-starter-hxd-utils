package com.hxd.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ESProperties.class,OSSProperties.class})
@Import(value = {ByteArrayUtils.class,
        EsRestClient.class,
        MD5Util.class,
        PDFUtils.class,
        RestClientUtil.class,
        SliceOfTime.class,
        StringUtil.class,
        TimeUtil.class,
        TokenUtil.class,
        OSSUtil.class
})

public class UtilsAutoConfiguration {
}
