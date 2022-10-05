package me.maskedvice;

import com.opencsv.bean.CsvBindByName;

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

    @CsvBindByName(column = "topicTags")
    public String TopicTags;

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

    //  getters, setters, toString
}
