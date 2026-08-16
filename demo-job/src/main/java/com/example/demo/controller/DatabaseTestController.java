package com.example.demo.controller;



	import org.neo4j.driver.Driver;
	import org.neo4j.driver.Session;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.RestController;

	@RestController
	public class DatabaseTestController {

	    private final Driver driver;

	    public DatabaseTestController(Driver driver) {
	        this.driver = driver;
	    }

	    @GetMapping("/api/test/db")
	    public String testDatabaseConnection() {

	        try (Session session = driver.session()) {

	            var record = session.run(
	                    "RETURN 1 AS result"
	            ).single();

	            int result = record.get("result").asInt();

	            return "CognoDB connection successful. Result = " + result;

	        } catch (Exception e) {

	            return "CognoDB connection failed: " + e.getMessage();
	        }
	    }
	}


