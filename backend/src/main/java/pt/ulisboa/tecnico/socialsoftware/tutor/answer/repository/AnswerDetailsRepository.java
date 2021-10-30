package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;

import java.util.Optional;

@Repository
@Transactional
public interface AnswerDetailsRepository extends JpaRepository<AnswerDetails, Integer> {
    @Query(value = "select * from answer_details where question_answer_id = :questionAnswerId", nativeQuery = true)
    Optional<AnswerDetails> findByQuestionAnswerId(Integer questionAnswerId);

    @Modifying
    @Query(value = "delete from answer_details ad where ad.question_answer_id = :questionAnswerId", nativeQuery = true)
    void deleteByQuestionAnswerId(int questionAnswerId);
}

