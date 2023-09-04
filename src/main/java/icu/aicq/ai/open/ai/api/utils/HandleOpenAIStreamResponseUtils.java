package icu.aicq.ai.open.ai.api.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import icu.aicq.ai.open.ai.api.common.OpenAIConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author zhiqi
 * @since 2023-03-24
 */
@Slf4j
public class HandleOpenAIStreamResponseUtils {


    public static void streamLine2CleanContent(String line, Consumer<String> cleanContent) {
        if (StringUtils.isNotBlank(line)) {
            String content = onlyContent(line);
            if (StringUtils.isNotEmpty(content)) {
                cleanContent.accept(content);
            }
        }
    }

    public static String onlyContent(String line) {
        if (StringUtils.isNotBlank(line)) {
            if (line.startsWith(OpenAIConstant.STREAM_DATA_STARTS_STR_PREFIX)) {
                line = line.replace(OpenAIConstant.STREAM_DATA_STARTS_STR_PREFIX, "");
            }
            if (StringUtils.isBlank(line)) {
                return null;
            }
            if (!JSON.isValid(line)) {
                return null;
            }
            try {
                JSONObject jsonObject = JSON.parseObject(line);
                JSONArray choices = EasyJsonUtils.getJSONArrayByRoute(jsonObject, "choices");
                StringBuffer stringBuffer = new StringBuffer();
                Optional.ofNullable(choices).ifPresent(arr -> {
                    for (int i = 0; i < choices.size(); i++) {
                        JSONObject item = arr.getJSONObject(i);
                        Optional.ofNullable(item).flatMap(obj -> Optional.ofNullable(EasyJsonUtils.getStringByRoute(obj, "delta.content"))).ifPresent(stringBuffer::append);
                    }
                });
                return stringBuffer.length() > 0 ? stringBuffer.toString() : null;
            } catch (Exception e) {
                log.error("json format error: {}", line);
            }
        }
        return null;
    }
}
