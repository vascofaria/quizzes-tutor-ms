package pt.ulisboa.tecnico.socialsoftware.answerservice.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@Transactional
public interface QuestionAnswerItemRepository extends JpaRepository<QuestionAnswerItem, Integer> {

    @Modifying
    @Query(value = "INSERT INTO question_answer_items (username, quiz_id, quiz_question_id, answer_date, time_taken, time_to_submission) values (:username, :quizId, :quizQuestionId, :answerDate, :timeTaken, :timeToSubmission)",
            nativeQuery = true)
    void insertQuestionAnswerItemOptionIdNull(String username, Integer quizId, Integer quizQuestionId,
                                              LocalDateTime answerDate, Integer timeTaken, Integer timeToSubmission);

}
