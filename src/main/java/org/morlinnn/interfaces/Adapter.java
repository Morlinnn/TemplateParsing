package org.morlinnn.interfaces;

/**
 * 自定义类必须实现该接口, 并如果存在构造时的逻辑, 确保在无参构造方法中存在逻辑
 */
public interface Adapter {
    /**
     * 自定义类应确保能够与同类正确的比较一致, 让 constant 的设置生效
     * @param adapter
     * @return
     */
    boolean equals(Adapter adapter);
}
