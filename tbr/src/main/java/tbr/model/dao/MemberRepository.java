package tbr.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.MemberDTO;

@Repository
public interface MemberRepository extends CrudRepository<MemberDTO, String> {
	
	List<MemberDTO> findMemberByIdEquals(String id);
 
	List<MemberDTO> findMemberByIdContaining(String id);
}
