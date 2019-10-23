package tbr.model.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
	@Id // Java Persistance의 ID를 써야함
	@Column(length = 10)
	private String id;
	@Column(length = 10, nullable = false)
	private String pw;
//	private String key;
}