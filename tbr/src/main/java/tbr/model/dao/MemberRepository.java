package tbr.model.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.Member;

@Repository
public interface MemberRepository extends CrudRepository<Member, String> {

}
