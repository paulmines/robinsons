package com.example.robinsons.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.robinsons.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
	boolean existsByUid(UUID uid);
}
