package gs.com.sub;

import com.gs.model.AbstractMarketData;
import com.gs.model.TickData;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.cluster.ClusterInfo;
import org.openspaces.core.cluster.ClusterInfoAware;
import org.openspaces.events.SpaceDataEventListener;
import org.openspaces.events.notify.SimpleNotifyContainerConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public   class MDListener  implements SpaceDataEventListener<TickData>, ClusterInfoAware {

    protected Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    @Qualifier("mdGigaSpace")
    protected GigaSpace mdGigaSpace;
    private int cores;
    //private Source source;

    private int leaseRenewalEventRetryCount;
    private int leaseRenewalEventRetryIntervalMs;

    private boolean enableLocalCache = true;

    private boolean level1Only = false;

    private Integer instanceId = 1;

    private boolean subscribedAllForInstance = false;


    private Map<String, TickData> snapshots = new ConcurrentHashMap<String, TickData>();

    //private Map<String, SimpleNotifyEventListenerContainer> notifyMap = new ConcurrentHashMap<String, SimpleNotifyEventListenerContainer>();

    SimpleNotifyEventListenerContainer notifyEventListenerContainer;

    //private MDClientProcessor[] processors;

    //public abstract void handleUpdate(TickData data);

   /* public TickData getMd(String symbol) {
        String symbolId = AbstractMarketData.getSymbolId(symbol, source);
        if (enableLocalCache && snapshots.containsKey(symbolId)) {
            return new TickData(snapshots.get(symbolId));
        }
        return null;
    }*/

    public void setEnableLocalCache(boolean enableLocalCache) {
        this.enableLocalCache = enableLocalCache;
    }

    @PostConstruct
    public void postConstuct(){
      /*  processors = new MDClientProcessor[cores];
        for (int index = 0; index < cores; index++) {
            processors[index] = new MDClientProcessor();
        }*/
    }

    @PreDestroy
    public void preDestroy() {
        /*clear();
        if (processors != null) {
            for (int index = 0; index < cores; index++) {
                if (processors[index] != null) {
                    processors[index].destroy();
                }
            }
        }*/
    }

    public void clear() {
        snapshots.clear();
    }

    //@Required//md.cores
    /*public void setCores(int cores) {
        this.cores = cores;
    }*/

    /*@Required
    public void setSource(Source source) {
        log.debug("MDListner.setSource({})", source);
        this.source = source;
    }

    public Source getSource() {
        return source;
    }*/

    @Required //md.leaseRenewalEventRetryCount
    public void setLeaseRenewalEventRetryCount(int leaseRenewalEventRetryCount) {
        log.info("MDListener.LeaseRenewalEventRetry({})" +  leaseRenewalEventRetryCount);
        this.leaseRenewalEventRetryCount = leaseRenewalEventRetryCount ;
    }

    @Required //md.leaseRenewalEventRetryIntervalMs
    public void setLeaseRenewalEventRetryIntervalMs(int leaseRenewalEventRetryIntervalMs) {
        log.info("MDListener.LeaseRenewalEventRetryIntervalMs({})"+ leaseRenewalEventRetryIntervalMs);
        this.leaseRenewalEventRetryIntervalMs = leaseRenewalEventRetryIntervalMs ;
    }

    /**
     * LeaseListener which helps us to notifications when registered space is restart or relocated.
     * So that we can re-register with the space
     * @author arunk
     *
     */
    private class MDLeaseListener implements LeaseListener {

        private MDListener mdListener;
        private boolean isInstanceBased;
        private String symbol;

        public MDLeaseListener(MDListener mdListener, boolean isInstanceBased) {
            super();
            this.mdListener = mdListener;
            this.isInstanceBased = isInstanceBased;
        }

        public MDLeaseListener(MDListener mdListener, String symbol) {
            super();
            this.mdListener = mdListener;
            this.symbol = symbol;
        }

        public void notify(LeaseRenewalEvent leaseEvent) {

            log.info("Re-registering for all symbols");
            int retry = MDListener.this.leaseRenewalEventRetryCount;
            do {
                try {
                    log.info("Notification is interrupted, lease can't be renew..Re-registering with RetryCount: {}" + retry);
                    unsubscribe();
                    mdListener.subscribeAllForInstance();
                    break;
                } catch (Exception ex) {
                    log.info("Exception while processing LeaseRenewalEvent notify" + ex);
                    try {
                        Thread.sleep(MDListener.this.leaseRenewalEventRetryIntervalMs);
                    } catch (InterruptedException ie) {
                        log.info("Exception " + ie);
                    }
                }
            } while (--retry > 0);

        }
    }



   /* public TickData getSnapshot(String symbol) {
        if (symbol == null) {
            return new TickData(symbol, source);
        }
        if(!TickData.isIndex(symbol)){
            TickData snap = getMd(symbol);
            if (snap != null) {
                return snap;
            }
        }
        MarketData data = read(MarketData.getSymbolId(symbol, source));
        return data != null ? new TickData(data) : new TickData(symbol, source);
    }

    private MarketData read(String symbolId){
        MarketData data = mdGigaSpace.readById(MarketData.class, symbolId);
        if (level1Only && data != null) {
            for(FID l2fid : L2Fids){
                data.remove(l2fid);
            }
        }
        return data;
    }*/




    public synchronized void subscribeAllForInstance(){
        subscribedAllForInstance = true;


        TickData template = new TickData();

        SimpleNotifyContainerConfigurer configurer = new SimpleNotifyContainerConfigurer(
                mdGigaSpace).template(template).eventListener(this)
                .performTakeOnNotify(false).notifyUpdate(true).notifyWrite(true).leaseListener(new MDLeaseListener(this, true)).autoRenew(true)
                .fifo(true);
        notifyEventListenerContainer= configurer.notifyContainer();

        log.info("Subscribed for notifications ");
    }



    public void unsubscribe() {
        log.info("Remove all notifiers");
        if (notifyEventListenerContainer.isActive()){
            notifyEventListenerContainer.shutdown();
        }

    }



    public void onEvent(TickData tickData, GigaSpace space, TransactionStatus tranStatus, Object obj) {
        try{
            Thread.sleep(15);
        }
        catch (Exception e){

        }
    }

   /* @AdminCommand(help = "")
    public AdminResult reset() {
        this.resetSubscriptions();
        return new AdminResult(Status.OK, "reset subscription complete");
    }*/

    public boolean isLevel1Only() {
        return level1Only;
    }

   /* public void setLevel1Only(boolean level1Only) {
        this.level1Only = level1Only;
    }*/

    public void setClusterInfo(ClusterInfo clusterInfo) {
        instanceId = clusterInfo.getInstanceId();
    }
}