package me.maskedvice;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;


public class Questions {

    @CsvBindByName(column = "questionId")
    public String QuestionID;

      @CsvBindByName(column = "title")
    public String Title;

    @CsvBindByName(column = "titleSlug")
    public String TitleSlug;

    @CsvBindByName(column = "isPaidOnly")
    public String isPaidOnly;

    @CsvBindByName(column = "difficulty")
    public String Difficulty;

    @CsvBindByName(column = "likes")
    public String Likes;

    @CsvBindByName(column = "dislikes")
    public String Dislikes;

    @CsvBindByName(column = "categoryTitle")
    public String CategoryTitle;

    @CsvBindByName(column = "acRate")
    public String AcRate;

    @CsvBindByName(column = "frontendQuestionId")
    public String FrontendQuestionId;

    @CsvBindAndSplitByName(column = "topicTags", splitOn = ";",elementType = String.class)
    public List<String> TopicTags;

    @CsvBindByName(column = "hasSolution")
    public String HasSolution;

    @CsvBindByName(column = "hasVideoSolution")
    public String HasVideoSolution;

    @CsvBindByName(column = "acRateRaw")
    public String AcRateRaw;

    @CsvBindByName(column = "totalAccepted")
    public String TotalAccepted;

    @CsvBindByName(column = "totalSubmission")
    public String TotalSubmission;

    @CsvBindByName(column = "likes ratio")
    public String LikesRatio;

    public boolean isUsed;
    //  getters, setters, toString

}
