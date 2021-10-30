# Performance during the process of answering a quiz.

## While the user is reponding the quiz, he needs to:
 -  Load the questions
 -  Send que answer he's clicking

## ANSWER THE QUIZ

There are 300 students of the same course execution trying to answer an IN_CLASS quiz with
code at the same time. The quiz is timed. Everytime a student chooses an answer a question
answer (QuestionAnswerItem) is sent. Question answers itmes must be always stored on average
in less than 1 second. Each student should take 1 minute on average answering each question
and with a standard deviation of 20 seconds. The student can submit 0(10%),1(70%),2(15%),
3(5%) answers per question. The quiz ends (Conclude Quiz) whenever the student chooses or
the time expires.

1. Source:

 - End User (Student)

2. Stimulus:

 - stochastic: QuestionAnswer
 - sporadic: QuizAnswer

3. Artifact:

 - GetQuestions Service, QuestionAnswer Service, QuizAnswer Service, StatementService

4. Environment:

 - Operational Mode: Peak Load

5. Response:

 - Add Answer to the logs (QuizAnswerItem)

6. Response Measure:

 - // TODO: PROF -> Max time to register an answer 


Test:

 - Load test with 300 students answering the quizz


## GET THE QUIZ

1. Source:

 - End User

2. Stimulus:

 - sporadic: GetQuizz

3. Artifact:

 - GetQuizz Service

4. Environment:

 - Operational Mode: Peak Load

5. Response:

 - User get the quiz

6. Response Measure:

 - // TODO: PROF -> Max time to get the quiz


## PERFORMANCE
1. write some scenarios for performance
    do some tries for 300/500
    1 for get quiz
    1 for answer the quiz
    in the scenarios consider throughput
    take into account that students need to login previously
    
2. see the current architecture of the code and draw an architecture
3. Write load tests to test the scenarios above

### The current system supports 300 students answering a quiz.

## Component And Connector

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/QuizAnswerUpgrade/architecture/ComponentAndConnector.png)
