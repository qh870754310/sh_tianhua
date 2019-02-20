package com.sjkj.sh_th.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sjkj.sh_th.utils.ZabbixHelper;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: qh
 * @Date: 2018/11/20 17:10
 * @Description: zabbix由2部分构成，zabbix server与可选组件zabbix agent。
 * (1)zabbix agent需要安装在被监视的目标服务器上，它主要完成对硬件信息或与操作系统有关的内存，CPU等信息的收集。
 * (2)zabbix server可以单独监视远程服务器的服务状态；同时也可以与zabbix agent配合，可以轮询zabbix agent主动接收监视数据（agent方式），同时还可被动接收zabbix agent发送的数据（trapping方式）。
 * java程序以HTTP方式向API请求数据
 * zabbix监控中的几个概念：
 * 1、hostid:每个被监控服务器唯一标识。可以根据ip/hostname获得,具体方法为代码中的getHostIdByIp/getHostIdByHostName。
 * 2、key：监控项名字，可以在zabbix平台自行查到。例如"system.cpu.util[,idle]"，"system.cpu.load[all,avg1]"，"system.swap.size[,free]"等等。
 * 3、itemid:根据hostid和key唯一缺确定，监控项的唯一标识。具体方法为代码中的getItemId(hostId, key)。
 * 4、value_type：数据类型，可以通过官方API item.get获取，history.get方法中就是传value_type给history参数。获取这个参数详见我代码中的方法getValueType(hostId, key)，使用这个参数参考getHistoryDataByItemId(itemId, valueType, time_from, time_till)。
 * ---------------------
 * https://www.zabbix.com/documentation/2.4/manual/api/reference/item/object
 * https://www.zabbix.com/documentation/2.4/manual/api/reference/history/get
 */
public class demo {

    private static String user = "Admin";//zabbix登录账号
    private static String password = "zabbix";//zabbix登录密码

    private static ZabbixApi zabbixApi;

    /**
     * http://zabbix服务器的主机IP/zabbix/api_jsonrpc.php
     */
    private static final String url = "http://192.168.1.144/zabbix/api_jsonrpc.php";

    public static void main(String[] args) throws Exception {
        /*zabbixApi = new DefaultZabbixApi(url);
        zabbixApi.init();

        boolean login = zabbixApi.login(user, password);
        System.out.println("login:" + login);

        String host = "192.168.1.107";
        JSONObject filter = new JSONObject();

        filter.put("host", new String[] { host });
        Request getRequest = RequestBuilder.newBuilder()
                .method("host.get").paramEntry("filter", filter)
                .build();
        JSONObject getResponse = zabbixApi.call(getRequest);
        System.out.println(getResponse);
        String hostid = getResponse.getJSONArray("result")
                .getJSONObject(0).getString("hostid");
        //hostid:每个被监控服务器唯一标识,可以根据ip/hostname获得,具体方法为代码中的getHostIdByIp/getHostIdByHostName。

        System.out.println(hostid);*/
        String ip = "192.168.1.144";
   //     getHostIdByIP(ip);

  //      getItemid();
        login();
   //     getHostGroup();
        System.out.println("===============服务器列表=================");
   //     getHost();
        System.out.println("============getHostByHostId=============");

    //        getHostByHostId("10261");
   //     getHistory();

   //     getAlertInfo();

  //      getTrigger();

  //      getItemid();

 //       getAlertInfo();

        getTrigger();


        /*String itemList = new ZabbixHelper(user, password, url).getItemList();
        System.out.println(itemList);*/

    }

    /**
     * 用户登录授权
     * 由于使用zabbixAPI必须进行用户登录验证，所以在调用接口之前，必须先部署一个zabbix后台，拿到一个用户信息（包括用户名和密码）
     */
    public static boolean login() {
        zabbixApi = new DefaultZabbixApi(url);
        // init方法中创建CloseableHttpClient客户端
        zabbixApi.init();
        boolean login = zabbixApi.login(user, password);
        if (!login) {
            System.out.println("login fail");
        } else {
            System.out.println("login:" + login);
        }
        return login;
    }

    /**
     * 获取所有主机组（hostgroup）
     */
    private static JSONArray getHostGroup() {

        Request request = RequestBuilder.newBuilder().method("hostgroup.get").paramEntry("output", "extend").build();
        JSONObject getResponse = zabbixApi.call(request);
        JSONArray result = getResponse.getJSONArray("result");
        System.out.println(getResponse);
        return result;
    }

    /**
     * 获取所有主机（host）
     */
    private static void getHost() {
        Request request = RequestBuilder.newBuilder().method("host.get").paramEntry("output", "extend").build();
        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);

    }

    /**
     *  hostid:每个被监控服务器唯一标识,可以根据ip/hostname获得,具体方法为代码中的getHostIdByIp/getHostIdByHostName。
     * @param hostId
     * @return
     */
    public static Object getHostByHostId(String hostId) {
        if (!login()) {
            login();
        }
        String hostid = hostId;
        JSONObject filter = new JSONObject();
        filter.put("hostid", new String[] { hostid });
        Request getRequest = RequestBuilder.newBuilder()
                .method("host.get").paramEntry("filter", filter)
                .build();
        JSONObject getResponse = zabbixApi.call(getRequest);
        System.out.println(getResponse);
        return getResponse.getJSONArray("result");
    }

    /**
     * 根据主机的IP获取hostid
     * @return
     */
    private static String getHostIdByIP(String ip) {
        if (!login()) {
            login();
        }
        String host = ip;
        JSONObject filter = new JSONObject();
        filter.put("host", new String[] { host });
        Request getRequest = RequestBuilder.newBuilder()
                .method("host.get").paramEntry("filter", filter)
                .build();
        JSONObject getResponse = zabbixApi.call(getRequest);
        System.out.println(getResponse);
        String hostid = getResponse.getJSONArray("result")
                .getJSONObject(0).getString("hostid");
        //hostid:每个被监控服务器唯一标识,可以根据ip/hostname获得,具体方法为代码中的getHostIdByIp/getHostIdByHostName。
        System.out.println(hostid);
        return hostid;

    }

    /**
     * 获取主机的监控项itemid
     * 上面的 output 是限制返回项，如果想要返回所有的主机信息，可以去掉 output 。
     */
    private static List getItemid() {
        if (!login()) {
            login();
        }
        /*String[] arr = {
                "vm.memory.size[available]",        // 内存可用值  (KB)
                "vm.memory.size[total]",            // 内存总数  (KB)
                "system.cpu.util[,idle]",           // 当前CPU IDLE值 (%)
                "vfs.fs.size[/,used]",              // 当前 / 盘使用值 (KB)
                "vfs.fs.size[/,total]"              // 当前 / 盘总数    (KB)
        };*/
        String[] arr = {
                "system.cpu.util[hrProcessorLoad.4]",           // 当前CPU IDLE值 (%)
                "vfs.fs.pused[storageUsedPercentage.1]",
                "vfs.fs.total[storageTotal.1]",
                "vfs.fs.used[storageUsed.1]"
        // 当前 / 盘总数    (KB)
        };
       List items_result = new ArrayList();
        Arrays.stream(arr).forEach(item -> {
            JSONObject filter = new JSONObject();
            filter.put("key_", new String[] { item });
            Request getRequest = RequestBuilder.newBuilder()
                    .method("item.get")
                    .paramEntry("output", "extend")
                    .paramEntry("hostids", 10265)
                    .paramEntry("search", filter)
                    .paramEntry("sortfield", "name")
                    .build();
            JSONObject getResponse = zabbixApi.call(getRequest);
            System.out.println(getResponse);
            JSONArray result = getResponse.getJSONArray("result");
            String itemid = result.getJSONObject(0).getString("itemid");
            System.out.println(itemid);
            items_result.add(itemid);
        });
        return items_result;
    }


    /**
     * 获取对应监控项的历史信息
     * 上一步中我们获取到了所有对应监控项的itemid。现在获取这些监控项的历史信息。
     * 这个接口中的信息是每分钟更新一次的，所以具体要去多久的信息看各自的需求
     */
    private static void getHistory() {
        if (!login()) {
            login();
        }
        String[] arr = {
                "vm.memory.size[available]",        // 内存可用值  (KB)
                "vm.memory.size[total]",            // 内存总数  (KB)
                "system.cpu.util[,idle]",           // 当前CPU IDLE值 (%)
                "vfs.fs.size[/,used]",              // 当前 / 盘使用值 (KB)
                "vfs.fs.size[/,total]"              // 当前 / 盘总数    (KB)
        };

        List items = getItemid();
        final int[] index = {0};
        Arrays.stream(arr).forEach(item -> {
            index[0] = Arrays.asList(arr).indexOf(item);
            int history;
            if ("system.cpu.util[,idle]".equals(item)) {
                history= 0;
            } else {
                history = 3;
            }

            Request getRequest = RequestBuilder.newBuilder()
                    .method("history.get")
                    .paramEntry("output", "extend")
                    .paramEntry("history", history)
                    .paramEntry("itemids", items.get(index[0]))
                    .paramEntry("sortfield", "clock")
                    .paramEntry("sortorder", "DESC")
                    .build();
            JSONObject getResponse = zabbixApi.call(getRequest);
            System.out.println("=========================");
            System.out.println(getResponse);
            JSONArray result = getResponse.getJSONArray("result");
        });
    }

    /**
     * 获取报警信息（alert）
     */
    public static void getAlertInfo() {
        Request request = RequestBuilder.newBuilder().method("alert.get").
                paramEntry("output","extend").
                paramEntry("time_from",1544058030).//从某个时间点开始
                paramEntry("time_till",(int) (System.currentTimeMillis() / 1000)).//截止某个时间点
         //       paramEntry("hostids", new String[]{"10275","10276"}).
                build();
        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);
        JSONArray result = getResponse.getJSONArray("result");
        System.out.println(result);
    }

    /**
     * 获取触发器信息（trigger）
     */
    private static void getTrigger() {
        //获取所有触发器信息
        Request request = RequestBuilder.newBuilder().method("trigger.get").paramEntry("output", "extend").build();

        //获取 监控项id为 24028、24015 的触发器信息
        Request build = RequestBuilder.newBuilder().method("trigger.get").paramEntry("output", "extend").paramEntry("itemids", new String[]{"29808", "29809"}).build();

        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);
        JSONArray result = getResponse.getJSONArray("result");
    }

    /**
     * 获取监控项信息（item）
     * 通过 Key 查询 Items
     * 从key中具有“system”一词的ID为“10112”的主机检索所有监控项
     */
    private static void getItem() {
        JSONObject json = new JSONObject();
        json.put("key_", new String[]{"system"});
        Request request = RequestBuilder.newBuilder().method("item.get").
                paramEntry("output", new String[]{"hostid","key_","itemid","lastvalue","prevvalue"})
                .paramEntry("hostids", new String[]{"10084"})
                .paramEntry("filter",json).build();
        //执行请求
        JSONObject resJson = zabbixApi.call(request);
        System.out.println(resJson);
        //处理结果
        String error = String.valueOf(resJson.get("error"));
        if (!StringUtils.isEmpty(error)) {
            System.out.println("调用zabbix接口出错");
        } else {
            System.out.println(resJson);
            JSONArray result = resJson.getJSONArray("result");
            String resultStr = result.toJSONString();
            System.out.println("结果：：：：："+resultStr);
        }
    }

}
