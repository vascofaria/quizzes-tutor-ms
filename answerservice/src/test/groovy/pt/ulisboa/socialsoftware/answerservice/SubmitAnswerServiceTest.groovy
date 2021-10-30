package pt.ulisboa.socialsoftware.answerservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.socialsoftware.answerservice.SpockTest
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerItemRepository
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerService
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerDto

@DataJpaTest
class SubmitAnswerServiceTest extends SpockTest{

    @Autowired
    QuestionAnswerService answerService

    @Autowired
    QuestionAnswerItemRepository answerItemRepository

    def setup() {
    }

    def "Submitting a statementAnswerDto"() {
        given:
        def statementAnswerDto = new StatementAnswerDto()
        statementAnswerDto.setUsername("Demo User")
        statementAnswerDto.setQuestionAnswerId(1)
        statementAnswerDto.setQuizQuestionId(1)
        statementAnswerDto.setTimeTaken(100)


        when:
        answerService.submitAnswer(1, statementAnswerDto)

        then:
        answerItemRepository.findAll().get(0).quizQuestionId == 1
        answerItemRepository.findAll().get(0).username == "Demo User"
        answerItemRepository.findAll().get(0).timeTaken == 100

    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}
