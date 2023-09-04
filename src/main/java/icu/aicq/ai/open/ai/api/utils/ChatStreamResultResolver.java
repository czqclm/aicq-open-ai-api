package icu.aicq.ai.open.ai.api.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import icu.aicq.ai.open.ai.api.common.OpenAIConstant;
import icu.aicq.ai.open.ai.api.common.OpenAIRoleEnum;
import icu.aicq.ai.open.ai.api.pojo.dto.MessageDTO;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * openAI stream 结果解析器
 *
 * @author zhiqi
 * @since 2023-03-28
 */
@Slf4j
public class ChatStreamResultResolver {


    public static ChatCompletionResponse convertStreamData2ChatCompletionResponse(@NonNull Collection<String> msgList) {
        ChatCompletionResponse response = null;
        // 消息的内容
        StringBuilder content = new StringBuilder();
        MessageDTO assistantMsg = MessageDTO.builder().role(OpenAIRoleEnum.ASSISTANT.getCode()).content("").build();
        ChatCompletionResponse.Choice assistantChoice = null;
        for (String line : msgList) {
            HandleOpenAIStreamResponseUtils.streamLine2CleanContent(line, content::append);
        }

        for (String line : msgList) {
            if (line.startsWith(OpenAIConstant.STREAM_DATA_STARTS_STR_PREFIX)) {
                line = line.replace(OpenAIConstant.STREAM_DATA_STARTS_STR_PREFIX, "");
            }
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (!JSON.isValid(line)) {
                continue;
            }
            try {
                JSONObject jsonObject = JSON.parseObject(line);
                ChatCompletionResponse source = jsonObject.toJavaObject(ChatCompletionResponse.class);
                if (Objects.isNull(response)) {
                    response = source;
                    if (Objects.isNull(response.getChoices())) {
                        ArrayList<ChatCompletionResponse.Choice> choiceList = new ArrayList<>();
                        assistantChoice = ChatCompletionResponse.Choice.builder().message(assistantMsg).build();
                        choiceList.add(assistantChoice);
                        response.setChoices(choiceList);
                    } else {
                        assistantChoice = response.getChoices().get(0);
                        assistantChoice.setMessage(assistantMsg);
                    }
                } else {
                    List<ChatCompletionResponse.Choice> choices = source.getChoices();
                    if (Objects.nonNull(choices) && Objects.nonNull(assistantChoice)) {
                        for (ChatCompletionResponse.Choice choice : choices) {
                            assistantChoice.setFinishReason(choice.getFinishReason());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("json format error: {}", line);
            }
        }
        if (Objects.nonNull(assistantChoice) && Objects.nonNull(assistantMsg)) {
            assistantMsg.setContent(content.toString());
        }
        return response;
    }
}
