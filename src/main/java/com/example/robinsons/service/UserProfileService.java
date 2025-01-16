package com.example.robinsons.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.robinsons.model.UserProfile;
import com.example.robinsons.repository.UserProfileRepository;

@Service
public class UserProfileService {

	@Autowired
	private UserProfileRepository userProfileRepository;
	
    public List<UserProfile> findAll() {
        return userProfileRepository.findAll();
    }
    
    public Optional<UserProfile> findByUid(UUID uid) {
        return userProfileRepository.findById(uid);
    }
    
    public boolean existsByUid(UUID uid) {
        return userProfileRepository.existsByUid(uid);
    }
	
    public UserProfile save(UserProfile book) {
        return userProfileRepository.save(book);
    }
    
    public void deleteById(UUID id) {
    	userProfileRepository.deleteById(id);
    }
}
