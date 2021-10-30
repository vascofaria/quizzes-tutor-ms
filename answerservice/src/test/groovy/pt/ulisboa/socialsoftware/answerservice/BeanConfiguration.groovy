package pt.ulisboa.socialsoftware.answerservice

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerService


@TestConfiguration
@PropertySource("classpath:application-test.properties")
class BeanConfiguration {


    @Bean
    QuestionAnswerService questionAnswerService() {
        return new QuestionAnswerService()
    }


}