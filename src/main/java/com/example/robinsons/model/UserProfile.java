package com.example.robinsons.model;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "user_profile")
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID uid;
	
	private String name;
	
	private String email_address;
	
	private String gender;
	
	@Temporal(TemporalType.DATE)
	private Date birthdate;
	
	private Integer age;
	
	private String role;
	
	public UserProfile() {
		
	}
	
	public UserProfile(String name, String email_address, String gender, Date birthdate,
			Integer age, String role) {
		this.name = name;
		this.email_address = email_address;
		this.gender = gender;
		this.birthdate = birthdate;
		this.age = age;
		this.role = role;
		
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	
	@Override
	public String toString() {
		return String.format("UserProfile{name='%s', email_address=%s, gender=%s, birthdate=%t, age=%d, role=%s}", this.name, this.email_address, this.gender, this.birthdate, this.age, this.role);
	}
	
}
