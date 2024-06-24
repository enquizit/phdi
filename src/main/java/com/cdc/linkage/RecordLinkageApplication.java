package com.cdc.linkage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.cdc.linkage.repository")
public class RecordLinkageApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecordLinkageApplication.class, args);
	}

}
