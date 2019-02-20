package com.sjkj.sh_th.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: qh
 * @Date: 2018/12/6 9:04
 * @Description:
 */
public class ApiUtils {

    private static String user = "Admin";//zabbix登录账号

    private static String password = "zabbix";//zabbix登录密码

    /**
     * http://zabbix服务器的主机IP/zabbix/api_jsonrpc.php
     */
    private static final String url = "http://192.168.1.144/zabbix/api_jsonrpc.php";

    private static ZabbixApi zabbixApi;

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
     * 获取zabbix中所以的主机群组列表
     * @return 主机群组列表json
     */
    public static Object getHostGroupList() {
        JSONObject filter = new JSONObject();
        filter.put("internal", new String[] { "0" });
        Request request = RequestBuilder.newBuilder().method("hostgroup.get")
                .paramEntry("output", "extend").paramEntry("filter", filter)
                .build();
        JSONObject response = zabbixApi.call(request);
        System.out.println(response);
        JSONArray result = response.getJSONArray("result");
        return result;
    }


    /**
     * 获取所有主机组（hostgroup）
     */
    public static void getHostGroup() {
        Request request = RequestBuilder.newBuilder().method("hostgroup.get").paramEntry("output", "extend").build();
        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);
    }

    /**
     * 获取所有主机（host）
     */
    public static Object getHost(String groupids) {
        Request request = RequestBuilder.newBuilder().method("host.get")
                .paramEntry("groupids", new String[] {groupids})
                .paramEntry("selectInventory", new String[]{"hardware_full"})
                .paramEntry("sortfield", new String[]{"name"})
                .paramEntry("output", "extend").build();
        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);
        return getResponse.getJSONArray("result");
    }

    /**
     *  hostid:每个被监控服务器唯一标识,可以根据ip/hostname获得,具体方法为代码中的getHostIdByIp/getHostIdByHostName。
     * @param hostId
     * @return
     */
    public static Object getHostByHostId(String hostId) {
        JSONObject filter = new JSONObject();
        filter.put("hostid", new String[] { hostId });
        Request getRequest = RequestBuilder.newBuilder()
                .method("host.get")
                .paramEntry("selectInventory", new String[]{"hardware_full","name", "alias", "asset_tag",  "vendor", "date_hw_decomm", "poc_1_cell"})
                .paramEntry("filter", filter)
                .build();
        JSONObject getResponse = zabbixApi.call(getRequest);
        System.out.println(getResponse);
        return getResponse.getJSONArray("result");
    }

    /**
     * 根据主机的IP获取hostid
     * @return
     */
    public static Object getHostByIP(String ip) {
        JSONObject filter = new JSONObject();
        filter.put("host", new String[] { ip });
        Request getRequest = RequestBuilder.newBuilder()
                .method("host.get").paramEntry("filter", filter)
                .build();
        JSONObject getResponse = zabbixApi.call(getRequest);
        System.out.println(getResponse);
   //     String hostid = getResponse.getJSONArray("result").getJSONObject(0).getString("hostid");
        //hostid:每个被监控服务器唯一标识,可以根据ip/hostname获得,具体方法为代码中的getHostIdByIp/getHostIdByHostName。
        return getResponse.getJSONArray("result");
    }


    public static Object getItemByHostIds(String hostIds, String nic) {
        String[] arr = {
                "UserPerfCountercpu",
                "vm.memory.size[]",           // 当前CPU IDLE值 (%)
                "vm.memory.size[pused]",
                "FreeTotalPerfCounter",
                "FreeTotalSpace",
                "agent.ping",
                "net.if.out[" + nic + "]",
                "net.if.in[" + nic + "]"
        };

        List items_result = new ArrayList();
        Arrays.stream(arr).forEach(item -> {
            JSONObject filter = new JSONObject();
            filter.put("key_", new String[] { item });
            Request getRequest = RequestBuilder.newBuilder()
                    .method("item.get")
                    .paramEntry("output", "extend")
                    .paramEntry("hostids", hostIds)
                    .paramEntry("search", filter)
                    .paramEntry("sortfield", "name")
                    .build();
            JSONObject getResponse = zabbixApi.call(getRequest);
            System.out.println(getResponse);
            items_result.add(getResponse.getJSONArray("result"));
        });
        return items_result;

    }

    /**
     * 获取主机的监控项itemid
     * 上面的 output 是限制返回项，如果想要返回所有的主机信息，可以去掉 output 。
     */
    private static List getItemid() {
        String[] arr = {
                "vm.memory.size[available]",        // 内存可用值  (KB)
                "vm.memory.size[total]",            // 内存总数  (KB)
                "system.cpu.util[,idle]",           // 当前CPU IDLE值 (%)
                "vfs.fs.size[/,used]",              // 当前 / 盘使用值 (KB)
                "vfs.fs.size[/,total]"              // 当前 / 盘总数    (KB)
        };
        List items_result = new ArrayList();
        Arrays.stream(arr).forEach(item -> {
            JSONObject filter = new JSONObject();
            filter.put("key_", new String[] { item });
            Request getRequest = RequestBuilder.newBuilder()
                    .method("item.get")
                    .paramEntry("output", "extend")
                    .paramEntry("hostids", 10084)
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
    public static Object getHistory() {
        String[] arr = {
                "vm.memory.size[available]",        // 内存可用值  (KB)
                "vm.memory.size[total]",            // 内存总数  (KB)
                "system.cpu.util[,idle]",           // 当前CPU IDLE值 (%)
                "vfs.fs.size[/,used]",              // 当前 / 盘使用值 (KB)
                "vfs.fs.size[/,total]"              // 当前 / 盘总数    (KB)
        };


        List items = getItemid();
        final int[] index = {0};
        List items_result = new ArrayList();
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
            System.out.println(getResponse);
            items_result.add(getResponse.getJSONArray("result"));
        });
        return items_result;
    }

    /**
     * 获取报警信息（alert）
     */
    public static Object getAlertInfo() {
        Request request = RequestBuilder.newBuilder().method("alert.get").
                paramEntry("output","extend").
                //    paramEntry("time_from",DateUtil.getTimeMs(6)/1000).//从某个时间点开始
                //    paramEntry("time_till",DateUtil.getTimeMs(5)/1000).//截止某个时间点
                //        paramEntry("hostids", new String[]{"10275","10109"})
                build();
        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);
        JSONArray result = getResponse.getJSONArray("result");
        return result;
    }

    /**
     * 获取触发器信息（trigger）
     */
    private static void getTrigger() {
        //获取所有触发器信息
        Request request = RequestBuilder.newBuilder().method("trigger.get").paramEntry("output", "extend").build();

        //获取 监控项id为 24028、24015 的触发器信息
        Request build = RequestBuilder.newBuilder().method("trigger.get").paramEntry("output", "extend").paramEntry("itemids", new String[]{"24028", "24015"}).build();

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

    public static Object getHostGroupByHostId(String hostId) {
        JSONObject filter = new JSONObject();
        filter.put("internal", new String[] { "0" });
        Request request = RequestBuilder.newBuilder().method("hostgroup.get")
                .paramEntry("hostids", new String[] { hostId })
                .paramEntry("output", "extend").paramEntry("filter", filter)
                .build();
        JSONObject response = zabbixApi.call(request);
        System.out.println(response);
        JSONArray result = response.getJSONArray("result");
        return result;
    }

    public static Object getHostByName(String name) {
        JSONObject filter = new JSONObject();
        filter.put("name", new String[] { name.toLowerCase() });
        Request request = RequestBuilder.newBuilder().method("host.get")
                .paramEntry("selectInventory", new String[]{"hardware_full"})
                .paramEntry("sortfield", new String[]{"name"})
                .paramEntry("output", "extend")
                .paramEntry("filter", filter).build();
        JSONObject getResponse = zabbixApi.call(request);
        System.out.println(getResponse);
        return getResponse.getJSONArray("result");
    }
}
