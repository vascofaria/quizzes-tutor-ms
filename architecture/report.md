## INTRO

In this project, we used an ADD iterative methodology to design a solution and a prototype for the current quizzes-tutor based on the requirements requested by the stakeholder (the professor) respectively performance, scalability, availability, and security for the use case Answer a class Quiz.


### **Running the microservices**

Create each database for each microservice:
```
dropdb answersdb1
createdb answersdb1

dropdb answersdb2
createdb answersdb2

dropdb answersdb3
createdb answersdb3
```

Run the monolith:
```
mvn clean spring-boot:run
```

Run the following commands on three different command line shells on answerservice directory:
```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--server.port=8083 --spring.datasource.url=jdbc:postgresql://localhost:5432/answersdb1"
mvn clean spring-boot:run -Dspring-boot.run.arguments="--server.port=8084 --spring.datasource.url=jdbc:postgresql://localhost:5432/answersdb2"
mvn clean spring-boot:run -Dspring-boot.run.arguments="--server.port=8085 --spring.datasource.url=jdbc:postgresql://localhost:5432/answersdb3"
```

## ORIGINAL Component And Connector of the system

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/develop/architecture/imgs/ComponentAndConnector-Original.png)

## PERFORMANCE during the process of answering a quiz.

In this part we found no need of further improvements because the transaction SubmitAnswer was already running on isolation level read uncommited during the quiz. So, the bottleneck was the postgres transaction management system.

### => Scenario

 - There are 300 students of the same course execution trying to answer an IN_CLASS quiz with code at the same time.
 - The quiz is timed.
 - Everytime a student chooses an answer a question answer (QuestionAnswerItem) is sent.
 - Question answers items must be always stored on average in less than 1 second.
 - Each student should take 1 minute on average answering each question and with a standard deviation of 20 seconds.
 - The student can submit 0(10%),1(70%),2(15%), 3(5%) answers per question.
 - The quiz ends (Conclude Quiz) whenever the student chooses or the time expires.

### => Component And Connector

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/develop/architecture/imgs/ComponentAndConnector-Original.png)

### => Tests

We wrote the test *300-students-in-class-quiz-answer-stress-old* to represent the scenario descrived above.

#### Throughput Results (transactions/second)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 47.44      | 253.26        | 45.38         |

#### Average latency Results (miliseconds)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 192.28     | 109.38        | 112.65        |

## SCALABILITY in terms of the number of simultaneous students answering a quiz.

In this requirement, we created 3 microservices with the essential services to the use case to obtain better scalability. Although we only focused on 3 microservices we could have more or less depending on the needs at a given moment. Also, we passed the necessary information to the use case for memory through the use of ConcurrentHashMaps instead of directly using the databases, that is, disk memory. Thus, in this way, we managed to significantly improve results in terms of both latency and throughput.

### => Scenario

 - There are 300/600/900/1200/1500 students of the same course execution trying to answer an IN_CLASS quiz with code at the same time.
 - The quiz is timed.
 - Everytime a student chooses an answer a question answer (QuestionAnswerItem) is sent.
 - Question answers items must be always stored on average in less than 1 second.
 - Each student should take 1 minute on average answering each question and with a standard deviation of 20 seconds.
 - The student can submit 0(10%),1(70%),2(15%), 3(5%) answers per question.
 - The quiz ends (Conclude Quiz) whenever the student chooses or the time expires.

### => Component And Connector

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/develop/architecture/imgs/ComponentAndConnector-Scalability.png)

### => Tests

We wrote several tests based on the performance test for 300/600/900/1200/1500 and we tested before and after the upgrades:
 - *300-students-in-class-quiz-answer-stress-old*
 - *600-students-in-class-quiz-answer-stress-old*
 - *900-students-in-class-quiz-answer-stress-old*
 - *1200-students-in-class-quiz-answer-stress-old*
 - *1500-students-in-class-quiz-answer-stress-old*
 - *300-students-in-class-quiz-answer-stress*
 - *600-students-in-class-quiz-answer-stress*
 - *900-students-in-class-quiz-answer-stress*
 - *1200-students-in-class-quiz-answer-stress*
 - *1500-students-in-class-quiz-answer-stress*

### Old Throughput Results (transactions/second)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 47.44      | 253.26        | 45.38         |
|  600       | 51.47      | 290.09        | 50.26         |
|  900       | 56.06      | 317.75        | 57.02         |
| 1200       | 59.38      | 346.06        | 60.78         |
| 1500       | 62.34      | 358.20        | 62.73         |

### Old Average latency Results (miliseconds)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 192.28     | 109.38        | 112.65        |
|  600       | 124.82     | 67.66         | 76.70         |
|  900       | 86.62      | 47.37         | 59.21         |
| 1200       | 85.01      | 47.35         | 56.21         |
| 1500       | 85.75      | 53.01         | 59.08         |

### New Throughput Results (transactions/second)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 45.28      | 262.25        | 47.04         |
|  600       | 62.70      | 357.96        | 63.99         |
|  900       | 75.45      | 431.90        | 76.66         |
| 1200       | 79.73      | 457.27        | 80.68         |
| 1500       | 79.04      | 453.30        | 79.99         |

### New Average latency Results (miliseconds)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 5.27       | 28.41         | 73.63         |
|  600       | 3.62       | 25.64         | 41.80         |
|  900       | 2.69       | 21.02         | 35.41         |
| 1200       | 2.37       | 20.33         | 33.20         |
| 1500       | 2.32       | 20.53         | 35.39         |

## AVAILABILITY of the system during the answer of a quiz.

To improve availability we had to sacrifice performance a bit and so the user can return to the state it was in when solving a quiz if the browser goes down. In order to display the results shortly after the quiz resolution we run a service, that requests QuestionsAnswerItems from the microservice, 15 seconds after the quiz conclusion date.

### => Scenario

 - A student is answering a quiz, answers a couple of questions and closes the browser.
 - The student reopens the browser and makes a request for the quiz and the quiz must have all the answers made by that student, so he can continue the quiz.
 - The quiz result must be available shortly after its completion.

### => Component And Connector

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/develop/architecture/imgs/ComponentAndConnector-Availability.png)

### => Tests

We wrote the test *student-quiz-reconect-answer-availability* that simulates a student answering 3 questions, leaving, then asking for the quiz again and getting the last question he was on, continue answering the quiz.

We didn't find the results of this test relevant for the report. It's just to simulate the user behaviour.

## SECURITY in terms of the possible attacks to the process of answering a quiz.

Our approach to security was more focused on the point of view of the user not being able to access all the questions at once and only being able to access one quiz question at a time. We ignore the problem of authenticating requests to microservices as it is a general issue for all projects and not the focal point for this project. In order to achieve this, the backend only sends a question at a time to the frontend. During the quiz whenever the user intends to proceed to the next question, the frontend requests a microservice for the next question. Previously, when the user requests a quiz, the quiz already brought all the questions and throught network inspection the user could see the next quetions in the DTO.

### => Scenario

 - The user is answering an in class quiz and tries to see all the questions at the same time, whithout answering the current one.

### => Component And Connector

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/develop/architecture/imgs/ComponentAndConnector-Security.png)

### => Tests

We just used the same tests as scalability but with the GetNextQuestion modification to see how it affects the value of latency and throughput:
 - *300-students-in-class-quiz-answer-stress-final*
 - *600-students-in-class-quiz-answer-stress-final*
 - *900-students-in-class-quiz-answer-stress-final*
 - *1200-students-in-class-quiz-answer-stress-final*
 - *1500-students-in-class-quiz-answer-stress-final*

### Throughput Results (transactions/second)

| N Students | Start Quiz | Get Next Question | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-----------------:|:-------------:|:-------------:|
|  300       | 41.94      | 208.3             | 238.84        | 43.32         |
|  600       | 55.58      | 276.85            | 318.55        | 56.53         |
|  900       | 66.33      | 329.91            | 378.76        | 67.42         |
| 1200       | 71.24      | 349.87            | 403.24        | 70.91         |
| 1500       | 74.64      | 373.02            | 429.05        | 75.46         |

### Average latency Results (miliseconds)

| N Students | Start Quiz | Get Next Question | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-----------------:|:-------------:|:-------------:|
|  300       | 4.97       | 3.07              | 22.9          | 37.57         |
|  600       | 3.39       | 2.37              | 21.94         | 35.49         |
|  900       | 2.43       | 1.91              | 19.41         | 28.86         |
| 1200       | 2.41       | 1.74              | 20.46         | 29.18         |
| 1500       | 2.07       | 1.62              | 18.26         | 29.03         |

## OTHER PROBLEMS

### Docker:
 - We noticed some downgrades in performance when using docker, this is probabling because docker needs to use more system calls to manager the containers, and also the restrictions of the docker service on the use of memory.

### MQTT:
 - We tried an aproach with an event broker called MQTT like the one showed in the picture below, but the overhead of writing in the event broker was very high. Due to this, the performance of submit answer in terms of both latency and throughput severally decreased.

### => Component And Connector

![Component And Connector](https://github.com/tecnico-softarch/softarch2020-05/blob/develop/architecture/imgs/ComponentAndConnector-Availability(Mosquitto).png)

### => Tests

Just for an example, we run this following *600-students-in-class-quiz-answer-stress-final* test and obtained the following results:

### Throughput Results (transactions/second)

| N Students | Start Quiz | Get Next Question | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-----------------:|:-------------:|:-------------:|
|  600       | 93.04      | 89.27             | 101.20        | 19.10         |

### Average latency Results (miliseconds)

| N Students | Start Quiz | Get Next Question | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-----------------:|:-------------:|:-------------:|
|  600       | 77.57      | 10.31             | 3974.07       | 227.76        |
