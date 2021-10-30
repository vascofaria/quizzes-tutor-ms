package com.example.QuizzesStore.quiz;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizService {
    Map<String, JSONObject> statementQuizzes = new ConcurrentHashMap<>();

    public void insertQuiz(JSONObject statementQuiz) throws JSONException {
        /*JSONObject statementQuiz = (JSONObject)userIdsStatementQuiz.get("statementQuiz");
        JSONArray userIds = (JSONArray)userIdsStatementQuiz.get("userIds");
        //Iterator<String> iterator = userIds.iterator();
        System.out.println(userIds);
        for (int i = 0; i < userIds.length(); i++) {
        }*/
        statementQuizzes.put(statementQuiz.get("id").toString()/* + "_" + userIds.opt(i).toString()*/, statementQuiz);

        System.out.println("Length: " + statementQuizzes.keySet());
        //System.out.println(statementQuizzes);
    }

    public JSONObject getQuiz(Integer quizId) throws QuizNotFoundException {
        if (statementQuizzes.containsKey(quizId.toString())) {
            return statementQuizzes.get(quizId.toString());
        } else {
            throw new QuizNotFoundException(quizId);
        }
    }
}
