package com.brick.yggdrasilserver.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileConfig implements WebMvcConfigurer {

    //jar包路径
    public static final String LOCATION = System.getProperty("user.dir");
    //系统
    public static final String OS = System.getProperty("os.name");
    //Windows文件路径
    public static final String IMAGE_WIN_PATH = LOCATION + "\\images";
    //Linux/MacOS文件路径
    public static final String IMAGE_LIN_PATH = LOCATION + "/images";

    //映射路径
    public static final String IMAGE_SKIN_PATTERN_PATH = "/csl";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        if (OS.toLowerCase().startsWith("win")) {
            registry.addResourceHandler("/csl/textures/**").addResourceLocations("file:" + IMAGE_WIN_PATH + "\\upload\\textures\\");
            registry.addResourceHandler("/cus/images/**").addResourceLocations("file:" + IMAGE_WIN_PATH + "\\");
        } else {
            registry.addResourceHandler("/csl/textures/**").addResourceLocations("file:" + IMAGE_LIN_PATH + "/upload/textures/");
            registry.addResourceHandler("/cus/images/**").addResourceLocations("file:" + IMAGE_LIN_PATH + "/");
        }
    }

}
