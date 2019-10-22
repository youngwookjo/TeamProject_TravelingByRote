package tbr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "member")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class MemberDTO {
	@Id // Java Persistance의 ID를 써야함
	@NonNull
	@Column(length = 10)
	private String id;
	@NonNull
	@Column(length = 10, nullable = false)
	private String pw;
	// Date : https://gmlwjd9405.github.io/2019/08/11/entity-mapping.html
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    
    // Many to Many in JPA https://blog.woniper.net/265
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "poking",
               joinColumns = @JoinColumn(name = "member_id"),
               inverseJoinColumns = @JoinColumn(name = "place_id"))
    @JsonBackReference
    private List<PlaceDTO> places = new ArrayList<>();
    
    public boolean addPlace(PlaceDTO place) {
        return places.stream().filter(v -> v.getId().equals(place.getId())).count() == 0? places.add(place) : false;
    }
    
    public boolean removePlace(PlaceDTO place) {
        return places.stream().filter(v -> v.getId().equals(place.getId())).count() != 0? places.remove(place) : false;
    }
}