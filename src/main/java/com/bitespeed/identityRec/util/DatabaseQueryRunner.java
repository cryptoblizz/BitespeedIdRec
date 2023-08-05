package com.bitespeed.identityRec.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseQueryRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseQueryRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        String sql = "SELECT NOW()";
        String result = jdbcTemplate.queryForObject(sql, String.class);
        System.out.println("Application is up and listening for POST request on http://localhost:8080/identity ");
        System.out.println("Current database time: " + result);
    }
}
