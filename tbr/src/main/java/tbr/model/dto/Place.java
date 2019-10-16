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
	@Column(precision=8)
	private BigDecimal id;
	@Column(precision=14)
	private BigDecimal mapx;//경도
	@Column(precision=14)
	private BigDecimal mapy;//위도
}
