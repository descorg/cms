package org.springcms.develop;

import org.springcms.core.launch.CmsApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevelopApplication {
    public static void main(String[] args) {
        CmsApplication.run("cms-develop", DevelopApplication.class, args);
    }
}