package test.model.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="place")
public class PlaceDTO {
	@Id
	@Column(precision=8)
	BigDecimal id;
	@Column(precision=2)
	BigDecimal typeId;
	String name;
	String address;
	@Column(precision=14)
	BigDecimal lat; // 위도
	@Column(precision=14)
	BigDecimal lon; // 경도
	String img;
	@Lob
	@Column(length=512)
	String description;
	
	public PlaceDTO(List<String> list, String name, String img) {
		this.id = new BigDecimal(list.get(0));
		this.typeId = new BigDecimal(list.get(1));
		this.name = name;
		this.img = img;
	}

}
