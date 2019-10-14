package tbr.model.dto;

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
public class Member {
	@Id // Java Persistance의 ID를 써야함
	@Column(name="member_id", length=10)
	private String id;
	@Column(length=10, nullable=false)
	private String pw;
	@Column(name="member_key", length=10)
	private String key;
}