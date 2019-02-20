package com.sjkj.sh_th.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sjkj.sh_th.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: qh
 * @Date: 2018/12/5 10:42
 * @Description:
 */
@RestController
@RequestMapping(value = "/api/")
public class ApiController {

    @Value("classpath:static/data/host_list.json")
    private Resource resource;

    /**
     * 获取主机组
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHostGroupList")
    public Map<String, Object> getHostGroupList() {
        Map<String, Object> map = new HashMap<>();
        Object hostGroupList = ApiUtils.getHostGroupList();
        map.put("result", hostGroupList);
        return map;
    }

    /**
     * 根据hostids获取主机组
     * @param hostId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHostGroupByHostId")
    public Map<String, Object> getHostGroupByHostId(String hostId) {
        Map<String, Object> map = new HashMap<>();
        Object hostGroupList = ApiUtils.getHostGroupByHostId(hostId);
        map.put("result", hostGroupList);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "getHostByName")
    public Map<String, Object> getHostByName(String name) {
        Map<String, Object> map = new HashMap<>();
        Object hostGroupList = ApiUtils.getHostByName(name);
        map.put("result", hostGroupList);
        return map;
    }

    /**
     * 获取主机列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHost")
    public Map<String, Object> getHost(String groupids) {
        Map<String, Object> map = new HashMap<>();
        Object host = ApiUtils.getHost(groupids);
        /*try {
            String s = IOUtils.toString(resource.getInputStream(), Charset.forName("UTF-8"));
            String jsonData = FileUtils.readFileToString(ResourceUtils.getFile("classpath:static/data/host_list.json"), Charset.forName("UTF-8"));
            JSONObject jsonObject = JSONObject.parseObject(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        map.put("result", host);
        return map;
    }

    /**
     * 通过主机id获取主机信息
     * @param hostId
     * @param nic 网卡键值
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHostByHostId")
    public Map<String, Object> getHostByHostId(String hostId, String nic) {
        Map<String, Object> map = new HashMap<>();
        Object host = ApiUtils.getHostByHostId(hostId);
        Object rate = ApiUtils.getItemByHostIds(hostId, nic);
        map.put("result", host);
        map.put("rate", rate);
        map.put("deviceName", "机架式服务器");
        map.put("code", "Dell R730");
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "getHostById")
    public Map<String, Object> getHostByHostId(String hostId) {
        Map<String, Object> map = new HashMap<>();
        Object host = ApiUtils.getHostByHostId(hostId);
        map.put("result", host);
        return map;
    }

    /**
     * 通过主机IP获取主机信息
     * @param ip
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHostByIp")
    public Map<String, Object> getHostByIp(String ip) {
        Map<String, Object> map = new HashMap<>();
        Object host = ApiUtils.getHostByIP(ip);
        map.put("result", host);
        return map;
    }

    /**
     * 查询利用率
     * @param hostIds
     * @param nic
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getItemByHostIds")
    public Map<String, Object> getItemByHostIds(String hostIds, String nic) {
        Map<String, Object> map = new HashMap<>();
        Object host = ApiUtils.getItemByHostIds(hostIds, nic);
        JSONArray detail = (JSONArray) ApiUtils.getHostByHostId(hostIds);
        JSONObject obj = (JSONObject) detail.get(0);
        JSONObject inventory = (JSONObject) obj.get("inventory");
        map.put("result", host);
        map.put("deviceName", inventory.getString("name"));
        map.put("code", inventory.getString("alias"));
        map.put("asset", inventory.getString("asset_tag")); //资产编号
        map.put("supplier", inventory.getString("vendor")); //供应商
        map.put("tel", inventory.getString("poc_1_cell")); //联系电话
        map.put("shelf_life", inventory.getString("date_hw_decomm")); //质保年限
        map.put("database_memory", Math.round(Math.random()*50) + "%"); //数据库内存
        return map;
    }

    /**
     * 告警信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAlertInfo")
    public Map<String, Object> getAlertInfo() {
        Map<String, Object> map = new HashMap<>();
        Object alert = ApiUtils.getAlertInfo();
        map.put("result", alert);
        return map;
    }

    /**
     * 查询监控线历史
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getHistory")
    public Map<String, Object> getHistory() {
        Map<String, Object> map = new HashMap<>();
        Object host = ApiUtils.getHistory();
        map.put("result", host);
        return map;
    }
}
