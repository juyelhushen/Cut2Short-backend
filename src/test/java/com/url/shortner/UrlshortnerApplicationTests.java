package com.url.shortner;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class UrlshortnerApplicationTests {

    @Test
    void contextLoads() {
    }

    @BeforeAll
    static void beforeAll() {
        log.info("beforeAll executed");
    }

    @BeforeEach
    void beforeEach() {
        log.info("beforeEach executed");
    }

    @Test
    void testCase1() {
        log.info("testCase1 executed");
    }

    @Test
    void testCase2() {
        log.info("testCase2 executed");
    }

    @AfterEach
    void afterEach() {
        log.info("afterEach executed");
    }

    @AfterAll
    static void afterAll() {
        log.info("afterAll executed");
    }

}
