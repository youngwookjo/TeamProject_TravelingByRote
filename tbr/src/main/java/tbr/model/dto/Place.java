package tbr.model.dto;

import java.math.BigDecimal;

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
public class Place {
	
			@Id @Column(length=10)@NotNull
			private  String id;
			@Column(length=10) @NotNull
			private  String pw;	
			
			@Column(length=10)
			private  String name;//이름
			
			@Column(length=15)
			private  String phonenumber;//전화번호
		
			private String homepage;//홈페이지
			
			@Column(length=20)
			private  BigDecimal mapx;//경도
			@Column(length=20)
			private  BigDecimal mapy;//위도
			@Column(length=30)
			private  String address;//주소
			@Column(length=20)
			private String image;//이미지의 주소값
			
			@Column(length=10)
			private BigDecimal typeid;//타입 id
			
		}

