package com.nousernameavailable.carprices.carprices.repository;

import com.nousernameavailable.carprices.carprices.model.Car;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends CrudRepository<Car, Long> {

    Optional<Car> findByCarIdSite(String carIdsite);

    Optional<Car> findByCarIdSiteAndModifiedIsNullAndDeletedIsNull(String carIdSite);

    @Query("select c from Car c where batch = :batchn and modified is not null and car_id_site not in :carIdSite ")
    Optional<List<Car>> findByNotCarIdSiteAndBatchAndModifiedIsNull(@Param("carIdSite") List<String> carIdSite, @Param("batchn") String bacth);

    boolean existsByCarIdSite(String carIdSite);


}
