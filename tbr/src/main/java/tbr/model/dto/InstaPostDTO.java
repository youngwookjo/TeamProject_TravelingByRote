package tbr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstaPostDTO {
	String locType;
	String img;
	String text;
	int likes;
	int comments;
}
