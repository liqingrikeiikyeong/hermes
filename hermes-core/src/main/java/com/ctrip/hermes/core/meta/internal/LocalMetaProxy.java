package com.ctrip.hermes.core.meta.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.unidal.lookup.annotation.Named;

import com.ctrip.hermes.core.bo.SchemaView;
import com.ctrip.hermes.core.bo.SubscriptionView;
import com.ctrip.hermes.core.bo.Tpg;
import com.ctrip.hermes.core.lease.Lease;
import com.ctrip.hermes.core.lease.LeaseAcquireResponse;

/**
 * for test only
 * 
 * @author Leo Liang(jhliang@ctrip.com)
 *
 */
@Named(type = MetaProxy.class, value = LocalMetaProxy.ID)
public class LocalMetaProxy implements MetaProxy {

	public final static String ID = "local";

	private AtomicLong m_leaseId = new AtomicLong(0);

	@Override
	public LeaseAcquireResponse tryAcquireConsumerLease(Tpg tpg, String sessionId) {
		long expireTime = System.currentTimeMillis() + 10 * 1000L;
		long leaseId = m_leaseId.incrementAndGet();
		return new LeaseAcquireResponse(true, new Lease(leaseId, expireTime), expireTime);
	}

	@Override
	public LeaseAcquireResponse tryRenewConsumerLease(Tpg tpg, Lease lease, String sessionId) {
		return new LeaseAcquireResponse(false, null, System.currentTimeMillis() + 10 * 1000L);
	}

	@Override
	public LeaseAcquireResponse tryRenewBrokerLease(String topic, int partition, Lease lease, String sessionId,
	      int brokerPort) {
		long expireTime = System.currentTimeMillis() + 10 * 1000L;
		long leaseId = m_leaseId.incrementAndGet();
		return new LeaseAcquireResponse(true, new Lease(leaseId, expireTime), expireTime);
	}

	@Override
	public LeaseAcquireResponse tryAcquireBrokerLease(String topic, int partition, String sessionId, int brokerPort) {
		long expireTime = System.currentTimeMillis() + 10 * 1000L;
		long leaseId = m_leaseId.incrementAndGet();
		return new LeaseAcquireResponse(true, new Lease(leaseId, expireTime), expireTime);
	}

	@Override
	public List<SchemaView> listSchemas() {
		return new ArrayList<SchemaView>();
	}

	@Override
	public List<SubscriptionView> listSubscriptions() {
		return new ArrayList<SubscriptionView>();
	}

	@Override
	public int registerSchema(String schema, String subject) {
		return -1;
	}

	@Override
	public String getSchemaString(int schemaId) {
		return null;
	}

}
