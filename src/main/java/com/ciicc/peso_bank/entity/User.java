package com.ciicc.peso_bank.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "username", length = 50, nullable = false)
	private String username;

	@Column(name = "password", length = 60, nullable = false)
	private String password;

	@Column(name = "user_status", length = 10, nullable = false)
	private String userStatus;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@OneToOne(
		mappedBy = "user", 
		cascade = CascadeType.ALL, 
		orphanRemoval = true
	)
	private Account account;

	@OneToOne(
		mappedBy = "user", 
		cascade = CascadeType.ALL,
		orphanRemoval = true
	
	)
	private UserProfile profile;

	@OneToMany(mappedBy = "user")
	private List<Audit> audits;

}
