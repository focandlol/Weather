package focandlol.weather.repository;

import focandlol.weather.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary,Integer> {

    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<Diary> getFirstByDate(LocalDate date);

    void deleteAllByDate(LocalDate date);

}
