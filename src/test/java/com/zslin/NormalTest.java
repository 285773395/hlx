package com.zslin;

import com.zslin.admin.dto.MyTicketDto;
import com.zslin.basic.tools.NormalTools;
import com.zslin.kaoqin.dto.DayDto;
import com.zslin.kaoqin.dto.MonthDto;
import com.zslin.kaoqin.model.Clockin;
import com.zslin.kaoqin.service.IClockinService;
import com.zslin.kaoqin.tools.*;
import com.zslin.sms.tools.RandomTools;
import com.zslin.sms.tools.SmsConfig;
import com.zslin.sms.tools.SmsTools;
import com.zslin.web.model.WeixinConfig;
import com.zslin.web.service.IBuffetOrderDetailService;
import com.zslin.web.service.IBuffetOrderService;
import com.zslin.web.tools.CommonTools;
import com.zslin.wx.tools.WxConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 钟述林 393156105@qq.com on 2017/1/24 10:35.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("zsl")
public class NormalTest {

    @Autowired
    private WxConfig wxConfig;

    @Autowired
    private KaoqinFileTools kaoqinFileTools;

    @Autowired
    private SmsTools smsTools;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private IBuffetOrderDetailService buffetOrderDetailService;

    @Autowired
    private IBuffetOrderService buffetOrderService;

    @Test
    public void test24() {
        Float f = buffetOrderService.queryBondByStatus("2017-06-20 16:00", "2017-06-20 23:30", "2"); //未退
        System.out.println("未退==============="+f);

        Float f2 = buffetOrderService.queryBondByStatus("2017-06-20 16:00", "2017-06-20 23:30", "5"); //有扣
        System.out.println("有扣==============="+f2);

        Float f3 = buffetOrderService.queryBondMoney("2017-06-20 16:00", "2017-06-20 23:30"); //所有
        System.out.println("==============="+f3);
    }

    @Test
    public void test23() {
        float f1 = (float)NormalTools.buildPoint(45/2f);
        System.out.println(f1);
        float f2 = (float)NormalTools.buildPoint(55/2f);
        System.out.println(f2);
    }

    @Test
    public void test22() {
        MyTicketDto mtd = new MyTicketDto(1, "d");
        Map<MyTicketDto, Integer> map = new HashMap<>();
        map.put(mtd, 3);
        System.out.println(map.containsKey(new MyTicketDto(1, "23")));
    }

    @Test
    public void test21() {
        Float f = buffetOrderService.queryMoneyByTicket("2017-06-11 09:00", "2017-06-11 15:30");
        System.out.println("==============="+f);

        Float f2 = buffetOrderService.queryMoneyByMeiTuan("2017-06-11 09:00", "2017-06-11 15:30");
        System.out.println("==============="+f2);
    }

    @Test
    public void test20() {
        Integer n1 = buffetOrderDetailService.queryCount("2017-06-08", "66666");
        Integer n2 = buffetOrderDetailService.queryCount("2017-06-08", "88888");
        System.out.println(n1+"============"+n2);
    }

    @Test
    public void test19() {
        smsTools.sendMsg(Integer.parseInt(smsConfig.getSendCodeIid()), "15925061256", "code", "123456");
    }

    @Test
    public void test16() {
        System.out.println(File.pathSeparator);
        System.out.println(File.separator);
    }

    @Test
    public void test() {
        WeixinConfig config = wxConfig.getConfig();
        System.out.println(config.getAppid()+"======"+config.getSecret()+"========="+config.getToken());
    }

    @Test
    public void test1() {
        String url = "http://www.zslin.com?id=1#zsl";
        System.out.println(url.substring(0, url.indexOf("#")));
    }

    @Test
    public void test2() {
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        System.out.println(path);
    }

    @Test
    public void test03() {
        String str = PicTools.getPicBase64("D:\\temp\\upload\\hlx\\deviceAdvert\\01bc98e3-c0c1-4ec3-bb1a-369cfc1061bb.jpg");
        System.out.println(str);
    }

    @Test
    public void test04() {
        String str = "{status:1,info:\"ok\",data:[{id:\"0\",do:\"update\",data:\"config\",name:\"玉盘珍\",company:\"昭通市玉盘珍餐饮有限公司\",companyid:1,max:3000,function:65535,delay:28,errdelay:10,timezone:+8,encrypt:0,expired:\"2055-12-10 12:10:10\"}}]}\n";
        System.out.println(str);
        System.out.println(str.substring(0, str.length()-2));
        System.out.println("ab\"c\n"+"===="+"ab\"c\n".length());
        String s = "\n";
        System.out.println("".equals(s.trim()));
    }

    @Test
    public void test05() {
        String con = GetJsonTools.buildCleanJson();
        kaoqinFileTools.setChangeContext(GetJsonTools.buildDataJson(con), false);
    }

    @Test
    public void test06() {
        String con = GetJsonTools.buildDeleteWorkerJson(3);
        kaoqinFileTools.setChangeContext(GetJsonTools.buildDataJson(con), false);
    }

    @Test
    public void test07() {
        String con = GetJsonTools.buildRebootDeviceJson();
        kaoqinFileTools.setChangeContext(GetJsonTools.buildDataJson(con), false);
    }

    @Test
    public void test08() {
        for(int i=0;i<20;i++) {
            System.out.println(RandomTools.randomNum4());
        }
    }

    @Test
    public void test09() {
        System.out.println(CommonTools.keep2Point(33d/10));
        System.out.println(CommonTools.daysOfTwo("2017-03-09", "2017-03-18"));
    }

    @Test
    public void test10() {
        String str = "{\n" +
                "  \"errcode\": 0,\n" +
                "  \"data\": {\n" +
                "    \"totalcount\": 16,\n" +
                "    \"pageindex\": 1,\n" +
                "    \"pagecount\": 8,\n" +
                "    \"records\": [\n" +
                "      {\n" +
                "        \"shop_id\": 429620,\n" +
                "        \"shop_name\": \"南山店\",\n" +
                "        \"ssid\": \"WX123\",\n" +
                "        \"ssid_list\": [\n" +
                "          \"WX123\",\n" +
                "          \"WX456\"\n" +
                "        ],\n" +
                "        \"protocol_type\": 4,\n" +
                "        \"sid\": \"\",\n" +
                "        \"poi_id\":\"285633617\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"shop_id\": 7921527,\n" +
                "        \"shop_name\": \"宝安店\",\n" +
                "        \"ssid\": \"\",\n" +
                "        \"ssid_list\": [],\n" +
                "        \"protocol_type\": 0,\n" +
                "        \"sid\": \"\",\n" +
                "        \"poi_id\":\"285623614\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        JSONObject jsonObj = new JSONObject(str);
        JSONObject data = jsonObj.getJSONObject("data");
        System.out.println(data.getInt("totalcount"));
        JSONArray array = data.getJSONArray("records");
        for(int i=0;i<array.length();i++) {
            JSONObject single = array.getJSONObject(i);
            System.out.println("single===="+single.getString("shop_name")+"===="+single.getInt("shop_id"));
        }
    }

    @Test
    public void test11() {
        String str = "91658502";
        Integer i = Integer.parseInt(str);
        System.out.println(str+"========"+i);
    }

    @Test
    public void test12() throws Exception {
        String day = NormalTools.curDate("yyyy-MM-dd");
        String str = "8:30";
        String s = day + " " + str;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = sdf.parse(s);
        System.out.println(s);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        System.out.println(cal.get(Calendar.YEAR)+"=="+cal.get(Calendar.MONTH)+"=="+cal.get(Calendar.DAY_OF_MONTH)+"=="+cal.get(Calendar.HOUR)+"=="+cal.get(Calendar.MINUTE)+"=="+cal.get(Calendar.SECOND));
//        System.out.println(d.getYear()+"=="+d.getMonth()+"=="+d.getDay()+"=="+d.getHours()+"=="+d.getMinutes()+"=="+d.getSeconds());
    }

    @Autowired
    private ClockinTools clockinTools;

    @Test
    public void test13() {
        String clockTime = "2017-04-20 08:22:59";
        clockinTools.clockin(1, clockTime, 1);
    }

    @Autowired
    private IClockinService clockinService;

    @Test
    public void test14() throws Exception {
        String month = "2017-03-";
        for(int i=12; i<=31;i++) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for(int j=1;j<=4;j++) {
                cal.setTime(sdf.parse(month + i));
                Clockin c = new Clockin();
                c.setDepId(1);
                c.setWorkerId(2);
                c.setWorkerName("张三");
                c.setVerify(1);
                c.setCurDay(sdf.format(cal.getTime()));
                c.setWeekday(getWeekday(cal));
                c.setDay(cal.get(Calendar.DAY_OF_MONTH));
                c.setMonth(cal.get(Calendar.MONTH)+1);
                c.setYear(cal.get(Calendar.YEAR));
                c.setTime("2017-04-20 08:22:59");
                c.setStep(j+"");
                c.setFlag(Math.random() < 0.4 ? 0 : 1);
                clockinService.save(c);
            }
        }
//        System.out.println(getWeekday("2017-03-15"));
    }

    private String getWeekday(Calendar cal) {
        try {
            String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};

            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void test15() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = "2017-3-5";
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(str));
        System.out.println(sdf.format(cal.getTime()));
        System.out.println(cal.get(Calendar.DAY_OF_WEEK));
    }

    /*@Test
    public void test16() {
        List<MonthDto> list = ClockinShowTools.initBefore(2017, 3, 1);
        System.out.println("3====="+list.size());
        list = ClockinShowTools.initBefore(2017, 3, 15);
        System.out.println("17====="+list.size());
        list = ClockinShowTools.initBefore(2017, 3, 18);
        System.out.println("20====="+list.size());
    }*/

    @Autowired
    private ClockinShowTools clockinShowTools;

    @Test
    public void test17() {
        MonthDto dto = clockinShowTools.buildWorkerClockin(2017, 3, 2);
        System.out.println(dto);
        for(DayDto d : dto.getList()) {
            System.out.println(d==null?"-":d);
        }
    }

    @Test
    public void test18() {
        String str = NormalTools.curDate("yyyy-MM");
        String [] array = str.split("-");
        System.out.println(array[0]+"======"+array[1]);
        System.out.println(Integer.parseInt(array[0])+"===="+Integer.parseInt(array[1]));
    }
}
