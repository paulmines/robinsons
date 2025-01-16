package com.example.robinsons.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.robinsons.model.UserProfile;
import com.example.robinsons.repository.UserProfileRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//activate automatic startup and stop of containers.
@Testcontainers
//JPA drop and create table, good for testing.
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class UserProfileControllerTest {

    @LocalServerPort
    private Integer port;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    // static, all tests share this postgres container.
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );
    
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;

        userProfileRepository.deleteAll();

        try {
        	// instance test data
			UserProfile userProfile = new UserProfile("test", "test@gmail.com", "male",  new SimpleDateFormat("yyyy-MM-dd").parse("1989-06-09"), 35, "test");
			// then save data in user profile.
			userProfileRepository.save(userProfile);
		} catch (ParseException e) {
			System.out.println("Invalid date format: " + e.getMessage());
		}
    }
    
    @Test
    public void testCreate() {
    	
    	Response response = given()
        .contentType(ContentType.JSON)
        .body("{ \"name\": \"test-created\", \"email_address\": \"test-created@gmail.com\", \"gender\": \"male\", \"birthdate\": \"1989-06-09\", \"age\": 35, \"role\": \"test-created\" }")
        .when()
            .post("/userprofiles")
        .then()
            .statusCode(201) // expecting HTTP 201 Created.
            .contentType(ContentType.JSON)
        .extract().response(); // expecting JSON response content.

    	Optional<String> uid = Optional.of(response.jsonPath().getString("uid"));
    	
        // find the new saved userprofile, to validate data creation.
        given()
                .contentType(ContentType.JSON)
                .pathParam("uid", uid.get())
                .when()
                    .get("/userprofiles/{uid}")
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(
                        ".", notNullValue(),
                        "name", equalTo("test-created"),
                        "email_address", equalTo("test-created@gmail.com"),
                        "gender", equalTo("male"),
                        "birthdate", equalTo("1989-06-09"),
                        "age", equalTo(35),
                        "role", equalTo("test-created")
                    );
    }
    
    @Test
    public void testGet() {
    	
    	// 1st validation.
    	Response response = given()
        .contentType(ContentType.JSON)
        .when()
            .get("/userprofiles")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(
                ".", hasSize(1)
    		)
        .extract().response(); // expecting JSON response content.
        
    	// extract userprofile from request response with list object.
    	List<UserProfile> userProfiles = response.jsonPath().getList("", UserProfile.class);
    	Optional<UserProfile> userProfile = Optional.of(userProfiles.get(0));
    	
    	// 2nd validation with uid parameter request.
    	given()
        .contentType(ContentType.JSON)
        .pathParam("uid", userProfile.get().getUid())
        .when()
            .get("/userprofiles/{uid}")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(
                ".", notNullValue(),
                "name", equalTo("test"),
                "email_address", equalTo("test@gmail.com"),
                "gender", equalTo("male"),
                "birthdate", equalTo("1989-06-09"),
                "age", equalTo(35),
                "role", equalTo("test")
    		)
        .extract().response(); 

    }
	
    
    @Test
    public void testUpdate() {
    	
    	// fetch single object for test data.
    	List<UserProfile> userProfiles = userProfileRepository.findAll();
    	Optional<UserProfile> userProfile = Optional.of(userProfiles.get(0));
    	//update test data.
    	userProfile.get().setName("test-updated");
    	userProfile.get().setEmail_address("test-updated@gmail.com");
    	userProfile.get().setGender("female");
    	userProfile.get().setAge(30);
    	try {
			userProfile.get().setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1989-06-06"));
		} catch (ParseException e) {
			System.out.println("Invalid date format: " + e.getMessage());
		}
    	userProfile.get().setRole("test-updated");
    	
    	// send test data to update in database.
        given()
        .contentType(ContentType.JSON)
        .body(userProfile.get())
        .when()
            .put("/userprofiles")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    	
        // fetch updated user to validate updated data.
        Optional<UserProfile> userProfileUpdated = userProfileRepository.findById(userProfile.get().getUid());
        
        assertEquals(userProfile.get().getName(), userProfileUpdated.get().getName());
        assertEquals(userProfile.get().getAge(), userProfileUpdated.get().getAge());
        assertEquals(userProfile.get().getBirthdate(), userProfileUpdated.get().getBirthdate());
        assertEquals(userProfile.get().getEmail_address(), userProfileUpdated.get().getEmail_address());
        assertEquals(userProfile.get().getGender(), userProfileUpdated.get().getGender());
        assertEquals(userProfile.get().getRole(), userProfileUpdated.get().getRole());        
        
    }
    
    @Test
    public void testDeleteByUid() {
       
    	// fetch any single object and delete
    	List<UserProfile> userProfiles = userProfileRepository.findAll();
    	Optional<UserProfile> userProfile = Optional.of(userProfiles.get(0));

        given()
        .pathParam("uid", userProfile.get().getUid())
        .when()
            .delete("/userprofiles/{uid}")
        .then()
            .statusCode(204); // expecting HTTP 204 No Content
        
        
    }
}
