package com.example.demo;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DemoApplicationTests {

	@SneakyThrows
	@Test
	void mainTest() {
		Rest rest = new Rest();
		rest.main();
	}

}