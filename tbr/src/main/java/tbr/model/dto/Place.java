package tbr.model.dto;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Place {
	@Id // Java Persistance의 ID를 써야함
	@Column(name="place_id", precision=10)
	private BigDecimal id;
	@Column(precision=10, nullable=false)
	private BigDecimal typeId;//타입 id
	@Column(length=100, nullable=false)
	private String name; // 장소명
	@Column(precision=20)
	private BigDecimal mapx;//경도
	@Column(precision=20)
	private BigDecimal mapy;//위도
}
