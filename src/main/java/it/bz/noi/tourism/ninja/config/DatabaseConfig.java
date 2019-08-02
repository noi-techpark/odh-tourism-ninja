package it.bz.noi.tourism.ninja.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan("it.bz.noi.tourism.ninja")
public class DatabaseConfig {

	@Bean
	DataSource dataSource() {
		HikariConfig config = new HikariConfig("/database.properties");
        HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}
}