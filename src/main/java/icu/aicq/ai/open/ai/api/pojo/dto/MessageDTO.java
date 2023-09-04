package icu.aicq.ai.open.ai.api.pojo.dto;

import icu.aicq.ai.open.ai.api.common.OpenAIRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhiqi
 * @since 2023-03-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {

    /**
     * 角色
     *
     * @see OpenAIRoleEnum
     */
    private String role;

    /**
     * 内容
     */
    private String content;

}
