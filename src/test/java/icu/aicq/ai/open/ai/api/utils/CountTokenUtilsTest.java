package icu.aicq.ai.open.ai.api.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import icu.aicq.ai.open.ai.api.pojo.dto.OpenAIUsageDTO;
import icu.aicq.ai.open.ai.api.pojo.req.ChatCompletionRequest;
import icu.aicq.ai.open.ai.api.pojo.rsp.ChatCompletionResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountTokenUtilsTest {

    @Test
    void countTokensByRequestAndResponse() {
        String request = "{\"messages\":[{\"content\":\"请帮我解释一下什么是 java 的反射\",\"role\":\"user\"}],\"model\":\"gpt-3.5-turbo\",\"stream\":true}";
        String response = "{\"choices\":[{\"delta\":{},\"finish_reason\":\"stop\",\"index\":0,\"message\":{\"content\":\"Java的反射是指在运行时（runtime）动态地获取Java对象的信息和操作Java对象的能力。反射机制允许程序在运行时动态加载、检查、创建和操作对象，而不需要在编译时就确定类型。使用反射机制可以动态地创建对象并取得成员变量和方法，也可以在运行时动态地获取类的信息、调用对象的方法和调整对象的质性。\\n\\n在Java中，每个类都有一个java.lang. Class类对象与之对应，可以通过这个对象来获取各种信息，如类的名字、方法、构造器、成员变量等。使用反射机制，我们可以操作这些信息，就可以在运行时动态地创建和获取对象，而不需要在编译时就确定类型。反射机制使得Java程序更加灵活，但是它会降低程序的性能和可读性，因此在实际应用中需要根据具体情况进行选择。\",\"role\":\"assistant\"}}],\"created\":1681179728,\"id\":\"chatcmpl-73xw00xR3liOFiDIn5vvmV5GfD052\",\"model\":\"gpt-3.5-turbo-0301\",\"object\":\"chat.completion.chunk\",\"usage\":{\"completionTokens\":17,\"promptTokens\":17,\"totalTokens\":34}}";


        ChatCompletionRequest chatCompletionRequest = JSONObject.parseObject(request, ChatCompletionRequest.class);
        ChatCompletionResponse chatCompletionResponse = JSONObject.parseObject(response, ChatCompletionResponse.class);

        OpenAIUsageDTO dto = CountTokenUtils.countTokensByRequestAndResponse(chatCompletionRequest, chatCompletionResponse);
        System.out.println("dto = " + JSON.toJSONString(dto));
    }
}