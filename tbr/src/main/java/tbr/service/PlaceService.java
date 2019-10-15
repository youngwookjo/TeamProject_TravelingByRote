package tbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tbr.model.dao.PlaceRepository;

@Service
public class PlaceService {
	@Autowired
	PlaceRepository placeRepository;
}
