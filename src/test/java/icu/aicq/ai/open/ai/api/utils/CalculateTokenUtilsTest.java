package icu.aicq.ai.open.ai.api.utils;


import org.junit.jupiter.api.Test;

class CalculateTokenUtilsTest {

    @Test
    void calculate() {
        System.out.println("tokens = " + CalculateTokenUtils.calculate("你好 GPT"));
    }
}