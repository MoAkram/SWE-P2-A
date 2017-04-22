package com.learn.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.learn.model.Achievement;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {
	@Query(value = "Select * from achievement where user_id=?1", nativeQuery = true)
	ArrayList<Achievement> FindAchiev(long user_id);
}
