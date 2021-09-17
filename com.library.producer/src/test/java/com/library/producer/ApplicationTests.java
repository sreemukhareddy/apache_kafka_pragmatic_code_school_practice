package com.library.producer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


class ApplicationTests {

	@Test
	void contextLoads() {
		Mono.just(List.of("a", "b", "c"))
			.flatMapMany(Flux::fromIterable)
				.subscribe(System.out::println);
	}

}
