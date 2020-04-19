package mainApplication.entityControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mainApplication.entities.Place;
import mainApplication.fusionAuth.FusionAuthController;
import mainApplication.repositories.PlaceRepository;

@RestController
@RequestMapping("place")
public class PlaceController {
	public final PlaceRepository placeRepo;
	private final FusionAuthController fusionAuthController;
	
	public PlaceController(PlaceRepository inPRepo, FusionAuthController inFusionAuth) {
		this.placeRepo = inPRepo;
		this.fusionAuthController = inFusionAuth;
	}
	
	@GetMapping("all")
	public ResponseEntity<Page<Place>> getAllPlaces(@RequestParam int pageNum){
		Pageable page = PageRequest.of(pageNum, 20, Sort.by("placeDesc"));
		
		return new ResponseEntity<>(placeRepo.findAll(page), HttpStatus.OK);		
	}
	
	@GetMapping("/{id}")
	public Optional<Place> getPlaceByID(@PathVariable Integer id){
		return placeRepo.findById(id);
	}
	
	public List<Place> addPlaces(List<Place> placeList){		
		List<Place> ret = new ArrayList<>();
		
		Place temp = null;
		for(Place place : placeList) {
			if(placeRepo.existsByLatitudeAndLongitude(place.getLatitude(), place.getLongitude()))
				temp = placeRepo.findByLatitudeAndLongitude(place.getLatitude(), place.getLongitude());
			else
				temp = placeRepo.save(place);
			
			ret.add(temp);
		}
		
		return ret;
	}
	
	public Place addSinglePlace(Place place) {
		Place temp = null;
		if(placeRepo.existsByLatitudeAndLongitude(place.getLatitude(), place.getLongitude()))
			temp = placeRepo.findByLatitudeAndLongitude(place.getLatitude(), place.getLongitude());
		else
			temp = placeRepo.save(place);
		
		return temp;
	}
	
	public boolean diffPlace(Place place) {
		Place temp = null;
		
		if(placeRepo.existsByLatitudeAndLongitude(place.getLatitude(), place.getLongitude())) {
			//Tested and this works
			temp = placeRepo.findByLatitudeAndLongitude(place.getLatitude(), place.getLongitude());
			
			if(place.getCountry().equals("") && place.getCounty().equals("") && place.getPlaceDesc().equals("") 
					&& place.getPlaceLOD().equals("") && place.getStateProv().equals("") && place.getTownCity().equals("")) {
				return false;
			}
			else if(place.equals(temp)) {
				return false;
			}
			else {
				return true;
			}
		}
		
		//Means that the place does not exist in the database
		return false;
	}
	
	@PostMapping("update")
	public ResponseEntity<String> updatePlace(@RequestBody Place place){//, @CookieValue(value = "access_token") Cookie cookie){
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!placeRepo.existsById(place.getPlaceID())) {
			return new ResponseEntity<>("This is not a valid place", HttpStatus.CONFLICT);
		}
		if(placeRepo.existsByLatitudeAndLongitude(place.getLatitude(), place.getLongitude()) 
				&& !placeRepo.findByLatitudeAndLongitude(place.getLatitude(), place.getLongitude()).getPlaceID().equals(place.getPlaceID())){
			return new ResponseEntity<>("The entered latitude and longitude pairing is already being used. Fix this and try again.", HttpStatus.CONFLICT);
		}
		
		placeRepo.saveAndFlush(place);
		
		return new ResponseEntity<>("Place was updated", HttpStatus.OK);
	}
	
	@PostMapping("delete")
	public ResponseEntity<String> deletePlace(@RequestBody Integer placeID){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!placeRepo.existsById(placeID)) {
			return new ResponseEntity<>("It looks like we showed you a place that does not exist in our database. Please refresh the page. If the place still "
					+ "shows, notify tech services.", HttpStatus.BAD_REQUEST);
		}
		
		placeRepo.deleteById(placeID);
		return new ResponseEntity<>("Place was deleted!", HttpStatus.OK);
	}
}
