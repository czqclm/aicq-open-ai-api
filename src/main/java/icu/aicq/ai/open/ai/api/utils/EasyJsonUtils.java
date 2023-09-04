package icu.aicq.ai.open.ai.api.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author zhiqi
 * @since 2022-05-20
 */
public class EasyJsonUtils {

    /**
     * 路由分隔符
     */
    public final static String ROUTE_DELIMITER = ".";

    public static JSONObject getJSONObjectByRoute(JSONObject jsonObject, String route) {
        LinkedList<String> queue = getQueueByRoute(route);
        return recursionFetchJSONObject(jsonObject, queue);
    }

    public static String getStringByRoute(JSONObject jsonObject, String route) {
        LinkedList<String> queue = getQueueByRoute(route);
        return recursionFetchString(jsonObject, queue);
    }

    public static JSONArray getJSONArrayByRoute(JSONObject jsonObject, String route) {
        LinkedList<String> queue = getQueueByRoute(route);
        return recursionFetchJSONArray(jsonObject, queue);
    }

    public static Object getByRoute(JSONObject jsonObject, String route) {
        LinkedList<String> queue = getQueueByRoute(route);
        return recursion(jsonObject, queue);
    }

    private static LinkedList<String> getQueueByRoute(String route) {
        if (StringUtils.isBlank(route)) {
            return null;
        }
        String[] nodes = route.split("\\" + ROUTE_DELIMITER);
        return new LinkedList<>(Arrays.asList(nodes));
    }

    public static JSONObject recursionFetchJSONObject(JSONObject jsonObject, LinkedList<String> queue) {
        if (null == jsonObject) {
            return null;
        }
        if (Objects.isNull(queue) || queue.isEmpty()) {
            return jsonObject;
        }
        if (1 == queue.size()) {
            return jsonObject.getJSONObject(queue.pop());
        }
        return recursionFetchJSONObject(jsonObject.getJSONObject(queue.pop()), queue);
    }


    public static String recursionFetchString(JSONObject jsonObject, LinkedList<String> queue) {
        if (null == jsonObject) {
            return null;
        }
        if (1 == queue.size()) {
            return jsonObject.getString(queue.pop());
        }
        return recursionFetchString(jsonObject.getJSONObject(queue.pop()), queue);
    }

    public static JSONArray recursionFetchJSONArray(JSONObject jsonObject, LinkedList<String> queue) {
        if (null == jsonObject) {
            return null;
        }
        if (1 == queue.size()) {
            return jsonObject.getJSONArray(queue.pop());
        }
        return recursionFetchJSONArray(jsonObject.getJSONObject(queue.pop()), queue);
    }

    public static Object recursion(JSONObject jsonObject, LinkedList<String> queue) {
        if (null == jsonObject) {
            return null;
        }
        if (1 == queue.size()) {
            return jsonObject.get(queue.pop());
        }
        return recursionFetchJSONArray(jsonObject.getJSONObject(queue.pop()), queue);
    }
}
