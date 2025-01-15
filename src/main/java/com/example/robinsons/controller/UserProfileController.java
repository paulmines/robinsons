package com.example.robinsons.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.robinsons.common.exception.InvalidInputException;
import com.example.robinsons.model.UserProfile;
import com.example.robinsons.service.UserProfileService;

@RestController
@RequestMapping("/userprofiles")
public class UserProfileController {

	@Autowired
	private UserProfileService userProfileService;
	
	
    @GetMapping
    public List<UserProfile> findAll() {
        return userProfileService.findAll();
    }
    
    @GetMapping("/{uid}")
    public Optional<UserProfile> findByUid(@PathVariable UUID uid) {
        return userProfileService.findByUid(uid);
    }
    
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public UserProfile create(@RequestBody UserProfile userProfile) {
        return userProfileService.save(userProfile);
    }
    
    @PutMapping
    public UserProfile update(@RequestBody UserProfile book) throws InvalidInputException {
    	
    	
    	if(userProfileService.existsByUid(book.getUid())) {
    	
            return userProfileService.save(book);
    		
    	} else {
    		throw new InvalidInputException("UserProfile with UID " + book.getUid() + " not found");
    	}
    	
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
    	userProfileService.deleteById(id);
    }
}
