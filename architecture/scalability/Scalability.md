# Scalability in terms of the number of simultaneous students answering a quiz.

## How many students can answer the quiz?

## ANSWER THE QUIZ - Scenario

There are 300 students of the same course execution trying to answer an IN_CLASS quiz with
code at the same time. The quiz is timed. Everytime a student chooses an answer a question
answer (QuestionAnswerItem) is sent. Question answers items must be always stored on average
in less than 1 second. Each student should take 1 minute on average answering each question
and with a standard deviation of 20 seconds. The student can submit 0(10%),1(70%),2(15%),
3(5%) answers per question. The quiz ends (Conclude Quiz) whenever the student chooses or
the time expires.

For 300 students, the system has a throughput of 28.46 transactions/s for starting a quiz, 165.82 transactions/s for submiting answers and 29.51 transactions/s for concluding a quiz. The system
should support more students (for instance 600,900,1200,1500) answering quiz without degrading these values of throughput.

## ANSWER THE QUIZ - Throughput Results (transactions/second)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 28.46      | 185.82        | 29.51         |
|  600       | 26.77      | 147.18        | 26.11         |
|  900       | 21.71      | 120.32        | 21.33         |
| 1200       | 16.14      | 87.86         | 15.49         |
| 1500       | 14.24      | 78.48         | 13.68         |

## ANSWER THE QUIZ - Average latency Results (miliseconds)

| N Students | Start Quiz | Submit Answer | Conclude Quiz |
|:----------:|:----------:|:-------------:|:-------------:|
|  300       | 1192.15    | 401.11        | 385.85        |
|  600       | 3635.81    | 1668.0        | 816.73        |
|  900       | 6667.83    | 2885.12       | 1196.14       |
| 1200       | 17387.42   | 6607.68       | 2055.75       |
| 1500       | 24998.85   | 9234.10       | 1953.41       |

1. Source:

 - End User

2. Stimulus:

 - stochastic: QuestionAnswer
 - sporadic: QuizAnswer

3. Artifact:

 - GetQuestions Service, QuestionAnswer Service, QuizAnswer Service

4. Environment:

 - Operational Mode: Peak Load

5. Response:

 - Add Answer to the logs

6. Response Measure:

 - // TODO: PROF -> Max time to register an answer 


Test:

 - 

SCALABILITY

1. Write scenarios
2. Rethink the architecture (thinking about introducing microservices)
    start by thinking about a modular monolitth

Redesign the architecture to take into account microservices
Do not rewrite tests without having precise scenarios.
