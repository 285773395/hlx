package com.zslin.wx.controller;

import com.zslin.basic.repository.SimplePageBuilder;
import com.zslin.basic.repository.SimpleSortBuilder;
import com.zslin.basic.repository.SimpleSpecificationBuilder;
import com.zslin.basic.tools.NormalTools;
import com.zslin.basic.utils.ParamFilterUtil;
import com.zslin.web.model.*;
import com.zslin.web.service.*;
import com.zslin.wx.dbtools.WalletDetailTools;
import com.zslin.wx.tools.AccountTools;
import com.zslin.wx.tools.EventTools;
import com.zslin.wx.tools.SessionTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/28 16:45.
 */
@Controller
@RequestMapping(value = "wx/food")
public class WeixinFoodController {

    @Autowired
    private IFoodService foodService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private EventTools eventTools;

    @Autowired
    private AccountTools accountTools;

    @Autowired
    private IWalletDetailService walletDetailService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IScoreRuleService scoreRuleService;

    @Autowired
    private WalletDetailTools walletDetailTools;

    @GetMapping(value = "index")
    public String index(Model model, Integer page, HttpServletRequest request) {

        System.out.println("======curOPenid:=="+ SessionTools.getOpenid(request));

        model.addAttribute("cateList", categoryService.findByOrder());
        Page<Food> datas = foodService.findAll(ParamFilterUtil.getInstance().buildSearch(model, request),
                SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("orderNo")));
        model.addAttribute("datas", datas);
        return "weixin/food/index";
    }

    //点赞
    @PostMapping(value = "goodFood")
    public @ResponseBody String goodFood(Integer id, HttpServletRequest request) {
        try {
            foodService.plusGoodCount(id);

            String openid = SessionTools.getOpenid(request);
            Account a = accountService.findByOpenid(openid);
            Food f = foodService.findOne(id);
            processScore(a, f.getName()); //处理积分和通知用户
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    @GetMapping(value = "detail")
    public String detail(Integer id, Integer page, Model model, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request); //当前用户的Openid
        model.addAttribute("food", foodService.findOne(id));
        //评论数据
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder("foodId", "eq", id);
        builder.add("status", "eq", "1", "and").add("openid", "eq", openid, "or");
        Page<Comment> datas = commentService.findAll(builder.generate(), SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("createDate_d")));
        model.addAttribute("datas", datas);
        return "weixin/food/detail";
    }

    @PostMapping(value = "add")
    public @ResponseBody String add(Integer foodId, String content, Integer isGood, HttpServletRequest request) {
        try {
            Food f = foodService.findOne(foodId);
            String openid = SessionTools.getOpenid(request);
            Account a = accountService.findByOpenid(openid);
            Comment comment = new Comment();
            comment.setAccountId(a.getId());
            comment.setContent(content);
            comment.setFoodId(foodId);
            comment.setFoodName(f.getName());
            comment.setFoodPic(f.getPicPath());
            comment.setHeadimgurl(a.getHeadimgurl());
            comment.setIsGood(isGood);
            comment.setNickname(a.getNickname());
            comment.setOpenid(openid);
            comment.setStatus("1");
            comment.setCreateDate(new Date());
            comment.setCreateLong(System.currentTimeMillis());
            comment.setCreateDay(NormalTools.curDate("yyyy-MM-dd"));
            comment.setCreateTime(NormalTools.curDate("yyyy-MM-dd HH:mm:ss"));
            commentService.save(comment);

            //TODO 事件通知管理人员，再通知点评者获得积分
            List<String> openidList = accountTools.getOpenid(AccountTools.ADMIN, AccountTools.MANAGER, AccountTools.PARTNER); //获取管理员、经理、股东
            StringBuffer sb = new StringBuffer();
            sb.append("被评食品：").append(f.getName()).append("\\n")
                .append("点评客户：").append(a.getNickname()).append("\\n")
                .append("点评级别：").append(isGood==1?"好评":"差评").append("\\n")
                .append("点评内容：").append(content);
            eventTools.eventRemind(openidList, "食品评论提醒", f.getName()+"被评论", NormalTools.curDate("yyyy-MM-dd HH:mm"), sb.toString(), "/wx/food/detail?id="+foodId);

            return "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private void processScore(Account a, String foodName) {
        ScoreRule sr = scoreRuleService.findByCode(ScoreRule.GOOD_FOOD); //
        if(sr!=null) {
            Integer count = walletDetailService.queryCount(ScoreRule.GOOD_FOOD, NormalTools.curDate("yyyy-MM-dd"), a.getOpenid());
            if(count<sr.getAmount()) {
                Integer score = sr.getScore();
                walletDetailTools.addWalletDetailScore(score, a.getOpenid(), a.getId(), a.getNickname(), ScoreRule.GOOD_FOOD, sr.getName());
                walletService.plusScore(score, a.getOpenid());

                StringBuffer sb = new StringBuffer();
                sb.append("积分增加：").append(score).append(" 分\\n")
                        .append("当前剩余：").append(walletService.queryScore(a.getOpenid())).append(" 分\\n")
                        .append("变化原因：").append(sr.getName()).append("\\n")
                        .append("点赞食品：").append(foodName);
                eventTools.eventRemind(a.getOpenid(), "积分变化提醒", "积分又增加了~~", NormalTools.curDate("yyyy-MM-dd HH:mm"), sb.toString(), "/wx/account/me");
            }
        }
    }
}