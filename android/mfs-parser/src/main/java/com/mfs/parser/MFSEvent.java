package com.mfs.parser;

/**
 * MFSEvent 对应 HSF 脚本中每一个具体的事件 (events 数组里的对象)
 * 比如：{ "time_ms": 12000, "event_ref": "R-104", "intensity": 0.8 }
 */
public class MFSEvent {

    // 必填字段
    private int timeMs;          // 触发时间（毫秒）
    private String eventRef;     // 事件编码，如 "R-104", "S-216"

    // 可选字段（如果脚本里没写，解析器会给默认值）
    private int durationMs;      // 持续时间（毫秒），-1 代表持续
    private double intensity;    // 强度 (0.0 - 1.0)

    // 构造方法 (用于创建一个事件对象)
    public MFSEvent(int timeMs, String eventRef, int durationMs, double intensity) {
        this.timeMs = timeMs;
        this.eventRef = eventRef;
        this.durationMs = durationMs;
        this.intensity = intensity;
    }

    // ---- 下面是 Getter 方法（供外部读取数据） ----
    public int getTimeMs() { return timeMs; }
    public String getEventRef() { return eventRef; }
    public int getDurationMs() { return durationMs; }
    public double getIntensity() { return intensity; }

    @Override
    public String toString() {
        return "MFSEvent{" +
                "timeMs=" + timeMs +
                ", eventRef='" + eventRef + '\'' +
                ", intensity=" + intensity +
                '}';
    }
}
