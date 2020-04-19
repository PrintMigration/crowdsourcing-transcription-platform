package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Place;

@Repository
@Transactional
public interface PlaceRepository extends JpaRepository<Place, Integer>, JpaSpecificationExecutor<Place> {
	boolean existsByLatitudeAndLongitude(double placeLat, double placeLong);
	Place findByLatitudeAndLongitude(double placeLat, double placeLong);
}
