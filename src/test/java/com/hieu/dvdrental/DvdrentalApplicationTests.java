package com.hieu.dvdrental;

import com.hieu.dvdrental.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfig.class)
class DvdrentalApplicationTests {

    @Test
    void contextLoads() {
    }

}
