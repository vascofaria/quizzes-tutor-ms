package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class writeQuestionAnswerFromQuestionAnswerItemTest extends SpockTest {

    def quiz
    def statementAnswerDto
    def user
    def course
    def courseExecution
    def statementAnswerDetailsDto
    def option
    def optionDto
    def quizDto
    def questionDto
    def quizQuestion
    def questionAnswer

    def setup() {
        course = new Course(Demo.COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, null)
        user = new User(DEMO_STUDENT_NAME, Demo.STUDENT_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.DEMO)
        courseExecution.addUser(user)
        user.addCourse(courseExecution)
        userRepository.save(user)
        courseExecutionRepository.save(courseExecution)


        quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setScramble(true)
        quizDto.setOneWay(true)
        quizDto.setQrCodeOnly(true)
        quizDto.setAvailableDate(STRING_DATE_TODAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto.setResultsDate(STRING_DATE_LATER)


        optionDto = new OptionDto()
        optionDto.setSequence(1)
        optionDto.setContent("Option Content")
        optionDto.setCorrect(true)
        option = new Option(optionDto)
        List<OptionDto> listOptions = new ArrayList<>();
        listOptions.add(optionDto)


        Question question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle(QUESTION_1_TITLE)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        option.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        optionRepository.save(option)



        questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quizDto.setQuestions(questions)
        quizDto.setTitle(QUIZ_TITLE)

        quiz = new Quiz(quizDto)
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        quizService.populateWithQuizAnswers(quiz.getId())

        quizQuestion = new QuizQuestion(quiz, question, 1)
        quizQuestionRepository.save(quizQuestion)

        questionAnswer = new QuestionAnswer(quiz.getQuizAnswers().toList().get(0), quizQuestion, 1)
        questionAnswerRepository.save(questionAnswer)
        statementAnswerDto = new StatementAnswerDto()
        statementAnswerDto.setQuizQuestionId(quiz.getQuizQuestions().toList().get(0).getId())
        statementAnswerDto.setQuestionAnswerId(quiz.getQuizAnswers().toList().get(0).getQuestionAnswers().get(0).getId())
        statementAnswerDto.setUsername(user.getUsername())

        statementAnswerDetailsDto = new MultipleChoiceStatementAnswerDetailsDto()
        statementAnswerDetailsDto.setOptionId(((MultipleChoiceQuestion)question.getQuestionDetails()).getOptions().get(0).getId())
        statementAnswerDto.setAnswerDetails(statementAnswerDetailsDto)
    }

    def "the quiz does not exist" () {
        given: "an invalid quiz id"
        def id = -1

        when:
        answerService.writeQuestionAnswerFromQuestionAnswerItem(id, statementAnswerDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.QUIZ_NOT_FOUND
    }


    def "the question answer does not exist" () {
        given: "an id"
        def id = quiz.getId()
        and: "an invalid optionId"
        statementAnswerDto.setQuestionAnswerId(-1)

        when:
        answerService.writeQuestionAnswerFromQuestionAnswerItem(id, statementAnswerDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.QUESTION_ANSWER_NOT_FOUND
    }

    def "the quiz, question answer and option exist complete the question answer" () {
        given: "an id"
        def id = quiz.getId()

        when:
        answerService.writeQuestionAnswerFromQuestionAnswerItem(id, statementAnswerDto)

        then: "check the option was inserted"
        def questionAnswer = questionAnswerRepository.findAll().toList().get(0)
        ((MultipleChoiceAnswer)questionAnswer.getAnswerDetails()).getOption().getId() == option.getId()
    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}