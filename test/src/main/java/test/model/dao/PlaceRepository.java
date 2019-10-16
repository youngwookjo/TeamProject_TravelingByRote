package test.model.dao;

import java.math.BigDecimal;

import org.springframework.data.repository.CrudRepository;

import test.model.dto.PlaceDTO;

public interface PlaceRepository extends CrudRepository<PlaceDTO, BigDecimal>{

}
