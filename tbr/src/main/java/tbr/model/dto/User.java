package tbr.model.dto;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
	public class User {
		@Id @Column(length=10)@NotNull
		private  String id;
		@Column(length=10) @NotNull
		private  String pw;	
		
		@Column(length=20, nullable = false)
		private  String wishlist;
	}


