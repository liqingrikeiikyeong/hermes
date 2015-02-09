package com.ctrip.hermes.broker.storage.pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ctrip.hermes.broker.storage.message.Ack;
import com.ctrip.hermes.broker.storage.message.Message;
import com.ctrip.hermes.broker.storage.range.DefaultRangeMonitor;
import com.ctrip.hermes.broker.storage.range.OffsetRecord;
import com.ctrip.hermes.broker.storage.range.RangeEvent;
import com.ctrip.hermes.broker.storage.range.RangeMonitor;
import com.ctrip.hermes.broker.storage.range.RangeStatusListener;
import com.ctrip.hermes.broker.storage.spi.Storage;
import com.ctrip.hermes.broker.storage.storage.Browser;
import com.ctrip.hermes.broker.storage.storage.Locatable;
import com.ctrip.hermes.broker.storage.storage.Offset;
import com.ctrip.hermes.broker.storage.storage.Range;
import com.ctrip.hermes.broker.storage.storage.StorageException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public abstract class AbstractPair<T extends Locatable> implements StoragePair<T>, RangeStatusListener {

    protected Storage<T> m_main;
    protected Storage<Offset> m_offset;
    private RangeMonitor m_offsetHandler;

    private Browser<T> m_mainBrowser;

    public AbstractPair(Storage<T> main, Storage<Offset> offset) {
        m_main = main;
        m_offset = offset;

        m_offsetHandler = new DefaultRangeMonitor();
        m_offsetHandler.addListener(this);

        Offset lastOffset = m_offset.top();
        m_mainBrowser = main.createBrowser(lastOffset == null ? Offset.OLDEST : lastOffset.getOffset() + 1);
    }

    @Override
    public List<T> readMain(int batchSize) throws StorageException {
        List<T> entities = Collections.emptyList();
        try {
            entities = m_mainBrowser.read(batchSize);
        } catch (Exception e) {
            throw new StorageException("", e);
        }

        return entities;
    }

    @Override
    public List<T> readMain(Range r) throws StorageException {
        return m_main.read(r);
    }

    @Override
    public void appendMain(List<T> payloads) throws StorageException {
        m_main.append(payloads);
    }

    @Override
    public void ack(OffsetRecord record, Ack ack) throws StorageException {
        m_offsetHandler.offsetDone(record, ack);
    }

    @Override
    public List<String> getStorageIds() {
        return Arrays.asList(m_main.getId(), m_offset.getId());
    }

    @Override
    public void addRangeStatusListener(RangeStatusListener listener) {
        m_offsetHandler.addListener(listener);
    }

    @Override
    public void appendMain(T payload) throws StorageException {
        m_main.append(Arrays.asList(payload));
    }

    @Override
    public void onRangeSuccess(RangeEvent event) throws StorageException {
        m_offset.append(Arrays.asList(event.getRecord().getToUpdate()));
    }

    @Override
    public void onRangeFail(RangeEvent event) throws StorageException {
        m_offset.append(Arrays.asList(event.getRecord().getToUpdate()));
    }

    @Override
    public void waitForAck(List<Message> msgs) {
        for (Message m : msgs) {
            m_offsetHandler.startNewRange(new OffsetRecord(m.getOffset(), m.getOffset()));
        }
    }

    @Override
    public void waitForAck(List<Message> msgs, Offset offset) {
        m_offsetHandler.startNewRange(new OffsetRecord(Lists.transform(msgs, new Function<Message, Offset>() {

            @Override
            public Offset apply(Message m) {
                return m.getOffset();
            }
        }), offset));
    }

}
