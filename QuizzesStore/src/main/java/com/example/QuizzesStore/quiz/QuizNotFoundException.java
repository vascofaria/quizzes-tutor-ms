package com.example.QuizzesStore.quiz;

public class QuizNotFoundException extends Exception{
    private Integer quizId;

    public QuizNotFoundException(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getQuizId() {
        return quizId;
    }
}
