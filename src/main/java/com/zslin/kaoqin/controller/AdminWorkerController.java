package com.zslin.kaoqin.controller;

import com.zslin.basic.annotations.AdminAuth;
import com.zslin.basic.annotations.Token;
import com.zslin.basic.repository.SimplePageBuilder;
import com.zslin.basic.repository.SimpleSortBuilder;
import com.zslin.basic.tools.MyBeanUtils;
import com.zslin.basic.tools.NormalTools;
import com.zslin.basic.tools.SecurityUtil;
import com.zslin.basic.tools.TokenTools;
import com.zslin.basic.utils.ParamFilterUtil;
import com.zslin.kaoqin.model.Worker;
import com.zslin.kaoqin.service.IWorkerService;
import com.zslin.kaoqin.tools.GetJsonTools;
import com.zslin.kaoqin.tools.KaoqinFileTools;
import com.zslin.web.model.Account;
import com.zslin.web.service.IAccountService;
import com.zslin.wx.tools.EventTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 钟述林 393156105@qq.com on 2017/2/27 16:47.
 */
@Controller
@RequestMapping(value="admin/appConfig")
@AdminAuth(name="员工信息维护", orderNum=10, psn="考勤管理", pentity=0, porderNum=20)
public class AdminWorkerController {

    @Autowired
    private IWorkerService workerService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private EventTools eventTools;

    @GetMapping(value = "list")
    @AdminAuth(name = "员工信息列表", type = "1", orderNum = 1, icon = "fa fa-users")
    public String list(Model model, Integer page, HttpServletRequest request) {
        Page<Worker> datas = workerService.findAll(ParamFilterUtil.getInstance().buildSearch(model, request),
                SimplePageBuilder.generate(page, SimpleSortBuilder.generateSort("id_d")));
        model.addAttribute("datas", datas);
        return "admin/worker/list";
    }

    @Token(flag= Token.READY)
    @AdminAuth(name = "添加员工", orderNum = 2, icon="fa fa-plus")
    @RequestMapping(value="add", method= RequestMethod.GET)
    public String add(Model model, HttpServletRequest request) {
        model.addAttribute("worker", new Worker());
        return "admin/worker/add";
    }

    @Token(flag= Token.CHECK)
    @RequestMapping(value="add", method=RequestMethod.POST)
    public String add(Model model, Worker worker, HttpServletRequest request) {
        if(TokenTools.isNoRepeat(request)) { //不是重复提交
            try {
                worker.setPassword(SecurityUtil.md5("123456789")); //所有员工默认密码为123456789
            } catch (NoSuchAlgorithmException e) {
            }

            bind(worker);

            workerService.save(worker);
            sendWorker2Device(worker);
        }
        return "redirect:/admin/worker/list";
    }

    @Token(flag= Token.READY)
    @AdminAuth(name="修改员工信息", orderNum=3, icon = "fa fa-pencil")
    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String update(Model model, @PathVariable Integer id, HttpServletRequest request) {
        Worker w = workerService.findOne(id);
        model.addAttribute("worker", w);
        return "admin/worker/update";
    }

    @Token(flag= Token.CHECK)
    @RequestMapping(value="update/{id}", method=RequestMethod.POST)
    public String update(Model model, @PathVariable Integer id, Worker worker, HttpServletRequest request) {
        if(TokenTools.isNoRepeat(request)) {
            Worker w = workerService.findOne(id);
            MyBeanUtils.copyProperties(worker, w);

            bind(w);

            workerService.save(w);
            sendWorker2Device(w);
        }
        return "redirect:/admin/worker/list";
    }

    @AdminAuth(name="删除员工信息", orderNum=4, icon = "fa fa-remove")
    @RequestMapping(value="delete/{id}", method=RequestMethod.POST)
    public @ResponseBody
    String delete(@PathVariable Integer id) {
        try {
            workerService.delete(id);
            sendDelWorker2Device(id); //从设备中删除员工数据
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    private void bind(Worker w) {
        try {
            Account a = accountService.findByPhone(w.getPhone());
            if(a!=null) {
                w.setAccountId(a.getId());
                w.setOpenid(a.getOpenid());
                w.setHeadimgurl(a.getHeadimgurl());
                eventTools.eventRemind(a.getOpenid(), "员工绑定提醒", "您已成功绑定员工信息", NormalTools.curDate(), "姓名："+w.getName()+"\\n电话："+w.getPhone(), "");
            }
        } catch (Exception e) {
        }
    }

    //写员工数据到设备
    private void sendWorker2Device(Worker w) {
        String content = GetJsonTools.buildDataJson(GetJsonTools.buildWorkerJson(w));
        KaoqinFileTools.setFileContext(content);
    }

    private void sendDelWorker2Device(Integer id) {
        String content = GetJsonTools.buildDataJson(GetJsonTools.buildDeleteWorkerJson(id));
        KaoqinFileTools.setFileContext(content);
    }
}