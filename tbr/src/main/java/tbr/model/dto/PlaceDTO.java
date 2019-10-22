package tbr.model.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="place")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
	@Id
	@Column(precision = 8)
	BigDecimal id;
	@Column(precision = 2)
	BigDecimal typeId;
	String name;
	String address;
	@Column(precision = 14, scale = 10)
	BigDecimal lat; // 위도
	@Column(precision = 14, scale = 10)
	BigDecimal lon; // 경도
	String img;
	@Lob
	@Column(length = 512)
	String description;
	long hits;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "places")
    @JsonBackReference
    // 상호참조 해결 : https://mycup.tistory.com/222
    private Set<MemberDTO> members;

	public PlaceDTO(List<String> list, String name, String img) {
		this.id = new BigDecimal(list.get(0));
		this.typeId = new BigDecimal(list.get(1));
		this.name = name;
		this.img = img;
	}

	public long addHit() {
		return hits++;
	}

}
