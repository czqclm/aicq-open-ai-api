package icu.aicq.ai.open.ai.api.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

/**
 * @author zhiqi
 * @date 2023-04-10
 */
public class CalculateTokenUtils {
    private final static EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private final static Encoding secondEnc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);

    public static Integer calculate(String content) {
        return secondEnc.countTokens(content);
    }
}
