package mainApplication.entityControllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import mainApplication.entities.Religion;
import mainApplication.repositories.ReligionRepository;

@RestController
@RequestMapping("religion")
public class ReligionController {
	private final ReligionRepository rrRepo;
	
	public ReligionController(ReligionRepository rrRepo) {
		this.rrRepo = rrRepo;
	}
	
	//Will be used to make religion a drop down in search
	@GetMapping
	public List<Religion> getAllReligions(){
		return rrRepo.findAll();
	}
}
