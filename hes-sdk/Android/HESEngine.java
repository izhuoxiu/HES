package com.zhuoxiu.hes;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;

public class HESEngine {
    private Vibrator vibrator;
    
    // 词典映射：存储所有30个标准词条的默认参数 (强度0~1, 时长ms, 波形类型)
    private static final Map<String, Object[]> DICT = new HashMap<>();
    static {
        // ===== 力量感 (Force) F-101 ~ F-106 =====
        DICT.put("F-101", new Object[]{0.2f, 15, "click"});    // 轻触
        DICT.put("F-102", new Object[]{0.5f, 30, "click"});    // 敲击
        DICT.put("F-103", new Object[]{0.8f, 80, "heavy"});    // 重击
        DICT.put("F-104", new Object[]{1.0f, 150, "burst"});   // 爆发
        DICT.put("F-105", new Object[]{0.6f, 200, "shake"});   // 震荡
        DICT.put("F-106", new Object[]{1.0f, 400, "crush"});   // 碾压
        // ===== 节奏感 (Rhythm) R-101 ~ R-106 =====
        DICT.put("R-101", new Object[]{0.4f, 30, "click"});
        DICT.put("R-102", new Object[]{0.5f, 60, "double"});   // 双连击
        DICT.put("R-103", new Object[]{0.6f, 300, "pulse"});   // 脉冲
        DICT.put("R-104", new Object[]{0.8f, 120, "heart"});   // 心跳
        DICT.put("R-105", new Object[]{0.5f, 1000, "wave"});   // 波浪
        DICT.put("R-106", new Object[]{0.4f, 500, "roll"});    // 滚奏
        // ===== 空间感 (Space) S-201 ~ S-206 =====
        DICT.put("S-201", new Object[]{0.9f, 1500, "approach"});
        DICT.put("S-202", new Object[]{0.9f, 1500, "retreat"});
        DICT.put("S-203", new Object[]{0.5f, 300, "through"});
        DICT.put("S-204", new Object[]{0.5f, 800, "orbit"});
        DICT.put("S-205", new Object[]{0.6f, 500, "expand"});
        DICT.put("S-206", new Object[]{0.6f, 500, "contract"});
        // ===== 运动感 (Motion) M-301 ~ M-306 =====
        DICT.put("M-301", new Object[]{0.8f, 1000, "rise"});
        DICT.put("M-302", new Object[]{0.8f, 1000, "fall"});
        DICT.put("M-303", new Object[]{0.6f, 800, "accel"});
        DICT.put("M-304", new Object[]{0.6f, 800, "decel"});
        DICT.put("M-305", new Object[]{0.1f, 2000, "hover"});
        DICT.put("M-306", new Object[]{0.8f, 50, "brake"});
        // ===== 情绪感 (Emotion) E-401 ~ E-406 =====
        DICT.put("E-401", new Object[]{0.5f, 600, "tension"});
        DICT.put("E-402", new Object[]{0.7f, 800, "oppress"});
        DICT.put("E-403", new Object[]{0.8f, 500, "release"});
        DICT.put("E-404", new Object[]{1.0f, 400, "excite"});
        DICT.put("E-405", new Object[]{0.15f, 1500, "calm"});
        DICT.put("E-406", new Object[]{0.5f, 300, "weird"});
    }

    public HESEngine(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 👇 开发者只需要调用这一行代码！
     * 例如：play("F-104", 1.0f)  -> 手机立刻爆发式震动
     * 例如：play("E-405", 0.5f)  -> 超轻柔的平静震动
     */
    public void play(String eventCode, float intensityScale) {
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (!DICT.containsKey(eventCode)) return; // 未知编码静默忽略

        Object[] params = DICT.get(eventCode);
        float baseIntensity = (float) params[0];
        int duration = (int) params[1];
        String type = (String) params[2];

        // 强度缩放（限制在0~1）
        float finalIntensity = Math.min(1.0f, baseIntensity * intensityScale);
        int amp = (int) (finalIntensity * 255); // Android振幅 0~255

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            switch (type) {
                case "burst":   // F-104 陡起缓落
                    vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 50, 100}, new int[]{0, 255, 0}, -1));
                    return;
                case "heart":   // R-104 咚哒心跳
                    vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 80, 100, 40}, new int[]{0, 255, 0, 128}, -1));
                    return;
                case "double":  // R-102 双连击
                    vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 30, 50, 30}, new int[]{0, amp, 0, amp}, -1));
                    return;
                case "pulse":   // R-103 脉冲 (0.5s开/0.3s关)
                    vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 500, 300}, new int[]{0, amp, 0}, 0));
                    return;
                case "wave":    // R-105 正弦波浪 (模拟)
                    vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 200, 200, 200, 200}, 
                        new int[]{0, amp/3, amp, amp/3, 0}, -1));
                    return;
                default:        // 通用：所有其他词条 (click, heavy, approach, tension, calm等)
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, amp));
                    return;
            }
        } else {
            // 安卓旧版本降级 (白皮书8.4)
            vibrator.vibrate(50);
        }
    }

    // 便捷重载：无需传强度，直接使用词典默认强度
    public void play(String eventCode) {
        play(eventCode, 1.0f);
    }
}
