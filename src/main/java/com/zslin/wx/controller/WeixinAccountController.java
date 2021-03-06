package com.zslin.wx.controller;

import com.zslin.basic.repository.SimplePageBuilder;
import com.zslin.basic.repository.SimpleSortBuilder;
import com.zslin.basic.repository.SimpleSpecificationBuilder;
import com.zslin.basic.tools.NormalTools;
import com.zslin.kaoqin.model.Worker;
import com.zslin.kaoqin.service.IWorkerService;
import com.zslin.sms.tools.RandomTools;
import com.zslin.sms.tools.SmsConfig;
import com.zslin.sms.tools.SmsTools;
import com.zslin.web.model.*;
import com.zslin.web.service.*;
import com.zslin.wx.dbtools.ScoreAdditionalDto;
import com.zslin.wx.dbtools.ScoreTools;
import com.zslin.wx.tools.AccountTools;
import com.zslin.wx.tools.QrTools;
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

/**
 * Created by 钟述林 393156105@qq.com on 2017/3/1 17:13.
 */
@Controller
@RequestMapping(value = "wx/account")
public class WeixinAccountController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IOwnService ownService;

    @Autowired
    private IWalletDetailService walletDetailService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private IQrcodeService qrcodeService;

    @Autowired
    private QrTools qrTools;

    @Autowired
    private SmsTools smsTools;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private ScoreTools scoreTools;

    @Autowired
    private IWorkerService workerService;

    @Autowired
    private IMemberChargeService memberChargeService;

//    @Autowired
//    private IOrdersService ordersService;

    @Autowired
    private IRulesService rulesService;

    @Autowired
    private IBuffetOrderService buffetOrderService;

    @Autowired
    private IWalletRemovedService walletRemovedService;

    //微信用户个人中心
    @GetMapping(value = "me")
    public String me(Model model, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        Account account = accountService.findByOpenid(openid);
        model.addAttribute("account", account);
        model.addAttribute("wallet", walletService.findByOpenid(openid));
        model.addAttribute("pullCount", accountService.findPullCount(account.getId()));
        model.addAttribute("ownCount", ownService.findCount(openid)); //礼物数量
        model.addAttribute("commentCount", commentService.findCount(openid)); //评论数量
        model.addAttribute("feedbackCount", feedbackService.findCount(openid)); //消息数量
        model.addAttribute("chargeCount", memberChargeService.findCount(openid)); //充值次数

        if(AccountTools.isPartner(account.getType())) {
            model.addAttribute("friendOrdersCount", buffetOrderService.findFriendCount(account.getPhone())); //友情折扣的次数
        }
        return "weixin/account/me";
    }

    //礼品列表
    @GetMapping(value = "own")
    public String own(Model model, Integer page, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder("openid", "eq", openid);
        Page<Own> datas = ownService.findAll(builder.generate(), SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("createDate_d")));
        model.addAttribute("datas", datas);
        model.addAttribute("ownCount", ownService.findCount(openid)); //礼物数量
        return "weixin/account/own";
    }

    @GetMapping(value = "score")
    public String score(Model model, Integer page, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder("openid", "eq", openid);
        builder.add("type", "eq", "2");
        Rules rules = rulesService.loadOne();
        Wallet w = walletService.findByOpenid(openid);
        model.addAttribute("wallet", w);
        model.addAttribute("canMoney", rules.getScoreMoney()==null|| rules.getScoreMoney()<=0?0:NormalTools.buildPoint(w.getScore()*1.0/rules.getScoreMoney()));
        Page<WalletDetail> datas = walletDetailService.findAll(builder.generate(),
                SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("createDate_d")));
        model.addAttribute("datas", datas);
        return "weixin/account/score";
    }

    @GetMapping(value = "money")
    public String money(Model model, Integer page, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder("openid", "eq", openid);
        builder.add("type", "eq", "1");
        model.addAttribute("wallet", walletService.findByOpenid(openid));
        Page<WalletDetail> datas = walletDetailService.findAll(builder.generate(),
                SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("createDate_d")));
        model.addAttribute("datas", datas);
        return "weixin/account/money";
    }

    @GetMapping(value = "commentList")
    public String commentList(Model model, Integer page, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder("openid", "eq", openid);
        Page<Comment> datas = commentService.findAll(builder.generate(),
                SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("createDate_d")));
        model.addAttribute("datas", datas);
        return "weixin/account/commentList";
    }

    @GetMapping(value = "feedbackList")
    public String feedbackList(Model model, Integer page, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        SimpleSpecificationBuilder builder = new SimpleSpecificationBuilder("openid", "eq", openid);
        Page<Feedback> datas = feedbackService.findAll(builder.generate(),
                SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("createDate_d")));
        model.addAttribute("datas", datas);
        return "weixin/account/feedbackList";
    }

    @GetMapping(value = "getQr")
    public String getQr(Model model, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        Account a = accountService.findByOpenid(openid);
        if(a!=null) {
            Qrcode qr = qrcodeService.findByOpenid(openid);
            if(qr==null) {
                String path = qrTools.genUserQr(a.getId() + "", a.getHeadimgurl());
                Qrcode qrcode = new Qrcode();
                qrcode.setOpenid(openid);
                qrcode.setAccountId(a.getId());
                qrcode.setNickname(a.getNickname());
                qrcode.setQrPath(path);
                qrcode.setName(a.getNickname());
                qrcode.setHeadimg(a.getHeadimgurl());
                qrcode.setCreateDate(new Date());
                qrcode.setCreateLong(System.currentTimeMillis());
                qrcode.setCreateDay(NormalTools.curDate("yyyy-MM-dd"));
                qrcode.setCreateTime(NormalTools.curDate("yyyy-MM-dd HH:mm:ss"));
                qr = qrcodeService.findByOpenid(openid);
                if(qr==null) {
                    qrcodeService.save(qrcode);
                }
            }
            return "redirect:/weixin/qr?id="+a.getId();
        }
        return "redirect:/weixin/index";
    }

    @PostMapping(value = "updateQrName")
    public @ResponseBody String updateQrName(String name, HttpServletRequest request) {
        if(name!=null && !"".equals(name.trim())) {
            String openid = SessionTools.getOpenid(request);
            qrcodeService.updateName(name, openid);
        }
        return "1";
    }

    @GetMapping(value = "modifyPhone")
    public String modifyPhone(Model model, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        Account a = accountService.findByOpenid(openid);
        model.addAttribute("account", a);
        return "weixin/account/modifyPhone";
    }

    @PostMapping(value = "sendCode")
    public @ResponseBody String sendCode(String phone, HttpServletRequest request) {
        try {
            Account a = accountService.findByPhone(phone);
            if(a!=null) {return "-1";}
            String code = RandomTools.randomNum4();
            request.getSession().setAttribute("sms_code", code);
            smsTools.sendMsg(Integer.parseInt(smsConfig.getSendCodeIid()), phone, "code", code);
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
        return "1";
    }

    @PostMapping(value = "modifyPhone")
    public @ResponseBody String modifyPhone(String phone, String code, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        String sessionCode = (String) request.getSession().getAttribute("sms_code");
        if(code.equals(sessionCode)) {
            //walletService.modifyPhone(phone, openid); //不能先修改数据
            updateData(phone, openid);
            updateWallet(phone, openid);
            //处理积分
            scoreTools.processScore(openid, ScoreRule.BIND_PHONE, new ScoreAdditionalDto("手机号码", phone));
            return "1";
        } else {
            return "0";
        }
    }

    //修改员工数据和微信用户数据
    private void updateData(String phone, String openid) {
        Worker w = workerService.findByPhone(phone);
        if(w!=null) {
            Account a = accountService.findByOpenid(openid);
            w.setHeadimgurl(a.getHeadimgurl());
            w.setOpenid(openid);
            w.setAccountId(a.getId());
            workerService.save(w);

            a.setName(w.getName());
            a.setIdentity(w.getIdentity());
            a.setPhone(phone);
            a.setBindPhone("1");
            if("0".equals(a.getType())) {
                a.setType("1");
            }
            accountService.save(a);
        } else {
            accountService.modifyPhone(phone, openid);
        }
    }

    private void updateWallet(String phone, String openid) {
        Wallet w = walletService.findByPhone(phone);
        Wallet wxWallet = walletService.findByOpenid(openid);
        if(w!=null && wxWallet!=null && !w.getId().equals(wxWallet.getId())) { //两个钱包都不为空，且不是同一个
            Account a = accountService.findByOpenid(openid);
            w.setMoney(w.getMoney()+wxWallet.getMoney());
            w.setOpenid(openid);
            w.setAccountId(a.getId());
            w.setScore(w.getScore()+wxWallet.getScore());
            w.setAccountName(wxWallet.getAccountName());
            walletService.save(w);

            a.setHasCard(w.getMoney()>0?"1":"2");
            accountService.save(a);

            WalletRemoved wr = new WalletRemoved();
            wr.setAccountName(wxWallet.getAccountName());
            wr.setScore(wxWallet.getScore());
            wr.setAccountId(wxWallet.getAccountId());
            wr.setOpenid(wxWallet.getOpenid());
            wr.setMoney(wxWallet.getMoney());
            wr.setPassword(wxWallet.getPassword());
            wr.setPhone(wxWallet.getPhone());
            wr.setRemoveTime(NormalTools.curDate());
            wr.setCreateDate(wxWallet.getCreateDate());
            wr.setCreateDay(wxWallet.getCreateDay());
            wr.setCreateLong(wxWallet.getCreateLong());
            wr.setCreateTime(wxWallet.getCreateTime());
            walletRemovedService.save(wr);

            walletService.delete(wxWallet);
        } else {
            walletService.modifyPhone(phone, openid);
        }
    }

    @PostMapping(value = "sharePage")
    public @ResponseBody String sharePage(String type, HttpServletRequest request) {
        String openid = SessionTools.getOpenid(request);
        if(openid==null || "".equals(openid)) {return "未检测到用户信息";}
        //type可以为SHARE和SHARE-FRIEND
        scoreTools.processScore(openid, type); //通知积分
        return "1";
    }
}
