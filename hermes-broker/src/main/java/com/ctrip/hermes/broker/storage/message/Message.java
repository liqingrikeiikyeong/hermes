package com.ctrip.hermes.broker.storage.message;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.hermes.broker.storage.storage.Locatable;
import com.ctrip.hermes.broker.storage.storage.Offset;
import com.ctrip.hermes.broker.storage.util.StringUtil;

public class Message implements Locatable {

    private static final int DEFAULT_PRIORITY = -1;
    private byte[] m_content;
    private Map<String, String> m_properties = new HashMap<String, String>();
    private Offset m_offset;
    private Offset m_ackOffset;

    public Offset getAckOffset() {
        return m_ackOffset == null ? m_offset : m_ackOffset;
    }

    public void setAckOffset(Offset ackOffset) {
        m_ackOffset = ackOffset;
    }

    public Offset getOffset() {
        return m_offset;
    }

    public void setOffset(Offset offset) {
        m_offset = offset;
    }

    public byte[] getContent() {
        return m_content;
    }

    public void setContent(byte[] content) {
        m_content = content;
    }

    public void setProperty(String key, String value) {
        m_properties.put(key, value);
    }

    public String getProperty(String key) {
        return m_properties.get(key);
    }

    public void setPriority(int priority) {
        setProperty(MessageConstants.PROP_PRIORITY, Integer.toString(priority));
    }

    public int getPriority() {
        return StringUtil.safeToInt(getProperty(MessageConstants.PROP_PRIORITY), DEFAULT_PRIORITY);
    }

}
