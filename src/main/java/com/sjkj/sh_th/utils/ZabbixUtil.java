package com.sjkj.sh_th.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: qh
 * @Date: 2018/11/20 16:18
 * @Description: Zabbix工具类
 */
public class ZabbixUtil {

    private static String URL = "http://192.168.1.144/api_jsonrpc.php";
    private static String AUTH = null;
    private static final String USERNAME = "Admin";
    private static final String PASSWORD = "zabbix";

    /**
     * 向Zabbix发送Post请求，并返回json格式字符串
     *
     * @param map 请求参数
     * @return
     */
    private static String sendPost(Map map) {
        String param = JSON.toJSONString(map);
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        BufferedReader reader = null;
        StringBuffer sb = null;
        try {
            //创建连接
            java.net.URL url = new URL(URL);
            connection  = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");// 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json");//设置发送数据的格式
            connection.connect();

            //POST请求
            out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(param);
            out.flush();

            //读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return sb.toString();
    }


    /**
     * 通过用户名和密码设置AUTH，获得权限 @
     */
    private static void setAuth() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", USERNAME);

        params.put("password", PASSWORD);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "user.login");
        map.put("params", params);
        map.put("auth", null);
        map.put("id", 0);

        String response = sendPost(map);
        JSONObject json = JSON.parseObject(response);
        AUTH = json.getString("result");
    }


    private static String getAuth() {
        if (AUTH == null) {
            setAuth();
        }
        return AUTH;
    }


    /**
     * 获得主机hostid
     *
     * @param host Zabbix中配置的主机hosts
     * @return 返回hostid @
     */
    private static String getHostIdByHostName(String host) {
        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put("host", host);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("output", "hostid");
        params.put("filter", filter);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "host.get");
        map.put("params", params);
        map.put("auth", getAuth());
        map.put("id", 0);

        String response = sendPost(map);
        JSONArray result = JSON.parseObject(response).getJSONArray("result");
        if (result.size() > 0) {
            JSONObject json = result.getJSONObject(0);
            String hostid = json.getString("hostid");
            return hostid;
        } else {
            return null;
        }

    }


    /**
     * 获得主机hostid
     *
     * @param ip Zabbix中配置的主机hosts
     * @return 返回hostid @
     */
    private static String getHostIdByIp(String ip) {
        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put("ip", ip);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("output", "extend");
        params.put("filter", filter);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "host.get");
        map.put("params", params);
        map.put("auth", getAuth());
        map.put("id", 0);

        String response = sendPost(map);
        JSONArray result = JSON.parseObject(response).getJSONArray("result");
        if (result.size() > 0) {
            JSONObject json = result.getJSONObject(0);
            String hostId = json.getString("hostid");
            return hostId;
        } else {
            return null;
        }

    }


    /**
     * 获得某主机的某个监控项id
     *
     * @param host 主机
     * @param item 监控项
     * @return 监控项itemid
     * @throws Exception
     */
    private static String getItemId(String hostId, String key) throws Exception {
        JSONArray result = getItemByHostAndKey(hostId, key);
        if (result.size() > 0) {
            JSONObject json = result.getJSONObject(0);
            if (json != null) {
                return json.getString("itemid");
            }
        }
        return null;

    }


    /**
     * 获取某主机某监控项的value_type
     *
     * @param host
     * @param key
     * @return
     * @throws Exception
     * @author xueyuan@meizu.com
     * @since 创建时间：2016年8月12日 上午10:38:10
     */
    private static int getValueType(String hostId, String key) throws Exception {
        JSONArray result = getItemByHostAndKey(hostId, key);
        if (result.size() > 0) {
            JSONObject json = result.getJSONObject(0);
            if (json != null) {
                return json.getIntValue("value_type");
            }
        }
        return 3;

    }


    /**
     * 获取某主机某监控项在一段时间内的数据
     *
     * @param itemId
     * @param valueType
     * @param from
     * @param to
     * @return
     * @author xueyuan@meizu.com
     * @since 创建时间：2016年8月18日 上午11:42:48
     */
    public static JSONArray getHistoryDataByItemId(String itemId, int valueType, long from, long to) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("output", "extend");
        params.put("history", valueType);
        params.put("itemids", itemId);
        params.put("sortfield", "clock");
        params.put("sortorder", "DESC");//时间降序
        params.put("time_from", from / 1000);
        params.put("time_till", to / 1000);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "history.get");
        map.put("params", params);
        map.put("auth", getAuth());
        map.put("id", 0);

        String response = sendPost(map);
        JSONArray result = JSON.parseObject(response).getJSONArray("result");
        System.out.println(result);
        return result;

    }


    /**
     * 获取某主机某监控项最近的数据
     *
     * @param hostId
     * @param key
     * @return @
     * @author xueyuan@meizu.com
     * @since 创建时间：2016年8月12日 上午10:31:49
     */
    public static JSONArray getItemByHostAndKey(String hostId, String key) {
        if (hostId == null) {
            return null;
        }
        Date start = new Date();

        Map<String, Object> search = new HashMap<String, Object>();
        search.put("key_", key);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("output", "extend");
        params.put("hostids", hostId);
        params.put("search", search);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "item.get");
        map.put("params", params);
        map.put("auth", getAuth());
        map.put("id", 0);

        String response = sendPost(map);
        JSONArray result = JSON.parseObject(response).getJSONArray("result");

        return result;
    }


    /**
     * 多个监控项的当前数据
     *
     * @param itemIds
     * @return
     * @author xueyuan@meizu.com
     * @since 创建时间：2016年8月18日 下午4:33:14
     */
    public static JSONArray getDataByItems(List<String> itemIds) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("output", "extend");
        params.put("itemids", itemIds);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "item.get");
        map.put("params", params);
        map.put("auth", getAuth());
        map.put("id", 0);
        String response = sendPost(map);
        JSONArray result = JSON.parseObject(response).getJSONArray("result");

        return result;

    }


    /**
     * 多个监控项的历史数据
     *
     * @param itemIds
     * @param valueType
     * @param from
     * @param to
     * @return
     * @author xueyuan@meizu.com
     * @since 创建时间：2016年8月18日 下午5:12:53
     */
    public static JSONArray getHistoryDataByItems(List<String> itemIds, int valueType, long from,
                                                  long to) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("output", "extend");
        params.put("history", valueType);
        params.put("itemids", itemIds);
        params.put("sortfield", "clock");
        params.put("sortorder", "DESC");//时间降序
        params.put("time_from", from / 1000);
        params.put("time_till", to / 1000);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonrpc", "2.0");
        map.put("method", "history.get");
        map.put("params", params);
        map.put("auth", getAuth());
        map.put("id", 0);
        String response = sendPost(map);
        JSONArray result = JSON.parseObject(response).getJSONArray("result");

        return result;

    }


    public static void main(String[] args) throws Exception {
        setAuth();
        long time_till = new Date().getTime();
        long time_from = time_till - 5 * 60000;
        String ip = "192.168.1.176";
        String key = "system.cpu.util[,idle]";
        String hostId = getHostIdByIp(ip);
        String itemId = getItemId(hostId, key);
        int valueType = getValueType(hostId, key);
        getHistoryDataByItemId(itemId, valueType, time_from, time_till);
    }
}
