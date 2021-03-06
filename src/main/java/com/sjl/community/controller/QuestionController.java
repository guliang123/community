package com.sjl.community.controller;

import com.sjl.community.dto.CommentDto;
import com.sjl.community.dto.QuestionDto;
import com.sjl.community.enums.CommentTypeEnum;
import com.sjl.community.enums.TopEnum;
import com.sjl.community.exception.CustomizeErrorCode;
import com.sjl.community.exception.CustomizeException;
import com.sjl.community.service.CommentService;
import com.sjl.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author song
 * @create 2020/2/18 20:18
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    /**
     * 根据问题id跳转到详情页面
     *
     * @param id
     * @return
     */
    @GetMapping("/question/{id}")
    public String showQuestion(@PathVariable("id") Long id, Model model) {
        //根据问题id查询详情
        QuestionDto questionDto = questionService.findById(id);
        //根据问题id查询评论详情
        List<CommentDto> commentDtos = commentService.findByQuestionId(id, CommentTypeEnum.TYPE_QUESTION);
        //根据问题tags查询相关问题
        List<QuestionDto> questionByTags = questionService.findByTags(questionDto);

        model.addAttribute("questionDto", questionDto);
        model.addAttribute("commentDtos", commentDtos);
        model.addAttribute("questionByTags", questionByTags);

        //增加浏览数
        questionService.addViewCount(id);
        return "question";
    }

    /**
     * 顶置问题
     *
     * @param oper
     * @param id
     * @return
     */
    @GetMapping("question/{oper}/{id}")
    public String setTop(@PathVariable("oper") Integer oper, @PathVariable("id") Long id) {
        if (!TopEnum.isNotExist(oper)) {
            questionService.setTopQuestion(oper, id);
            return "redirect:/";
        }
        throw new CustomizeException(CustomizeErrorCode.IS_NOT_LEGAL);
    }
}
