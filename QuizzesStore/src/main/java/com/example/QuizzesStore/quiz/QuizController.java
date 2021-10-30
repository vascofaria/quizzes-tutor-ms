package com.example.QuizzesStore.quiz;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;


    @PostMapping("/quiz/insert")
    public void insertQuiz(@RequestBody String statementQuiz) throws JSONException {
        JSONObject statementQuizDto = new JSONObject(statementQuiz);
        quizService.insertQuiz(statementQuizDto);
    }

    @GetMapping("/quiz/{quizId}/get")
    public String getQuiz(@PathVariable int quizId) throws QuizNotFoundException {
        return quizService.getQuiz(quizId).toString();
    }
}
