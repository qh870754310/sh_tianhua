package com.sjkj.sh_th;

import com.sjkj.sh_th.utils.ApiUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ShThApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShThApplication.class, args);
        ApiUtils.login();
    }
}
