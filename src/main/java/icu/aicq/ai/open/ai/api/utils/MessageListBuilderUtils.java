package icu.aicq.ai.open.ai.api.utils;

import icu.aicq.ai.open.ai.api.pojo.dto.MessageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 消息列表构建者
 *
 * <pre>{@code
 * List<MessageDTO> messageDTOList = MessageListBuilderUtils().builder().role("123").content("abc").next().role("456").content("def").build();
 * }</pre>
 *
 * @author zhiqi
 * @since 2024-07-23
 */
public class MessageListBuilderUtils {

    private List<MessageDTO> messageDTOList;

    private MessageDTO messageDTO;

    public static MessageListBuilderUtils builder() {
        MessageListBuilderUtils messageListBuilderUtils = new MessageListBuilderUtils();
        messageListBuilderUtils.messageDTOList = new ArrayList<>();
        return messageListBuilderUtils;
    }

    public MessageListBuilderUtils next() {
        if (Objects.nonNull(this.messageDTO)) {
            messageDTOList.add(this.messageDTO);
        }
        this.messageDTO = new MessageDTO();
        return this;
    }

    public MessageListBuilderUtils role(String role) {
        if (Objects.isNull(messageDTO)) {
            this.messageDTO = new MessageDTO();
        }
        this.messageDTO.setRole(role);
        return this;
    }

    public MessageListBuilderUtils content(String content) {
        if (Objects.isNull(messageDTO)) {
            this.messageDTO = new MessageDTO();
        }
        this.messageDTO.setContent(content);
        return this;
    }


    public List<MessageDTO> build() {
        if (Objects.nonNull(this.messageDTO)) {
            messageDTOList.add(this.messageDTO);
        }
        return this.messageDTOList;
    }
}
