package com.gs.model;

import java.util.Calendar;

public class TickData extends AbstractMarketData {

    private static final long serialVersionUID = 1L;

    public static final String MD_TYPE_NAME = "MarketData";

    public static final String SYMBOL = "symbol";
    public static final String TICKID = "tickid";
    public static final String SOURCE = "source";
    public static final String SYMBOLID = "symbolid";
    public static final String TIMESTAMP = "timeStamp";
    public static final String BUCKET = "bucket";
    public static final String TICKDATA = "tickData";
    public static final String TICKSEQUENCE = "tickSequence";


   /* public static final FID[] configuredMDTagsForPersistence = {
            FID.BID, //
            FID.BID_SIZE, //
            FID.ASK, //
            FID.ASK_SIZE, //
            FID.TRADE, //
            FID.TRADE_SIZE, //
            FID.PERCENT_CHANGE, //
            FID.BASE_PRICE, //
            FID.NOMINAL_PRICE, //
    };*/

    //public DeltaFids delta;

    public boolean reloadTickData;

    private boolean isSnapshot = false;

    public TickData() {
        super();
    }

    public TickData(String symbol, Source source) {
        this(symbol, source, 1);
    }

    public TickData(String symbol, Source source, int cores) {
        super(symbol, source, cores, 1, /*new FidData(),*/ Calendar.getInstance()
                .getTime(), System.nanoTime());
       // delta = new DeltaFids();
    }

    public TickData(AbstractMarketData amd) {
        super(amd.getSymbol(), amd.getSource(), 1, amd.getLevel(),/* new FidData(amd.getData()),*/
                amd.getTimestamp(), amd.getStartProcessTime());
        coreInstance = amd.coreInstance;
       /* if (amd instanceof TickData) {
            delta = new DeltaFids(((TickData) amd).delta.getFids());
            reloadTickData=((TickData) amd).isReloadTickData();
        } else {
            delta = amd.getData().isEmpty() ? new DeltaFids() : new DeltaFids(amd.getData().keySet());
        }*/
    }

    public void update(AbstractMarketData amd) {
        level = amd.getLevel();
        timestamp = amd.getTimestamp();
        startProcessTime = amd.getStartProcessTime();
        coreInstance = amd.getCoreInstance();
       // delta = amd.getData().isEmpty() ? new DeltaFids() : new DeltaFids(amd.getData().keySet());
       // data.putAll(amd.getData());
        if (amd instanceof TickData) {
            reloadTickData=((TickData) amd).isReloadTickData();
        }
    }

    /*public void update(FID fid, Double value) {
       // setMap();// tests use default constructor
        put(fid, value);
        if (delta == null) {
            delta = new DeltaFids();
        }
        delta.add(fid);
    }*/

    /*protected void shiftFIDUp(FID... list) {
        if (list.length < 1) {
            return;
        }
        for (int i=0; i<list.length-1; i++) {
            copyFidWithAbsent(list[i+1], list[i]);
        }
        remove(list[list.length-1]);
    }*/

    /*protected void copyFidWithAbsent(FID from, FID to){
        if(!isFidSet(from)){
            remove(to);
        } else {
            update(to, get(from));
        }
    }*/


    public boolean isReloadTickData() {
        return reloadTickData;
    }

    public void setReloadTickData(boolean reloadTickData) {
        this.reloadTickData = reloadTickData;
    }

    /*public Number get(FID fid, Number defaultValue) {
        return Number.defaultIfNull(getAsNumber(fid), defaultValue);
    }

    public Number getAsNumber(FID fid) {
        return Number.getNumber(super.get(fid));
    }*/

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TickData ");
        super.toString(sb);
        return sb.toString();
    }

    /*public boolean isFidChanged(FID... fids) {
        if(delta != null){
            for (FID fid : fids) {
                if(delta.exists(fid)){
                    return true;
                }
            }
        }
        return false;
    }*/

    /* Function to write and read from AUDIT space, don't need everything
    public FidData getTickDataForPersistence() {
        FidData data = new FidData();
        for (FID fid : configuredMDTagsForPersistence) {
            Double val = get(fid);
            if (val != null) {
                data.put(fid, val);
            }
        }
        return data;
    }

    @Override
    public void setImageStatus(boolean status) {
        update(FID.IMAGE_STATUS, status ? 1.0 : 0.0);
    }

    public boolean isClosePublished() {
        // For Osaka the BID_CONDITION handles the AM Close... For TSE and NG
        // all L2 FIDS are reset
        // BID1,ASK1,BID2,BID3,ASK2,ASK3,BID4,ASK4,BID5,ASK5,BID6,ASK6,BID7,ASK7,BID8,ASK8
        return checkContainNull(FID.BID1, FID.ASK1, FID.BID2, FID.ASK2, FID.BID3, FID.ASK3,
                FID.BID4, FID.ASK4, FID.BID5, FID.ASK5, FID.BID6, FID.ASK6, FID.BID7, FID.ASK7,
                FID.BID8, FID.ASK8);
    }

    @Override
    public Double remove(FID fid) {
        delta.remove(fid);
        return super.remove(fid);
    }

    public EnumSet<FID> getChangedFIDs() {
        return delta.getFids();
    }

    public static boolean isIndex(final String symbol){
        return symbol.startsWith("=");
    }*/

    /*
    public boolean isSuspended() {
        return StateInterpreter.isSuspended(get(FID.STATE));
    }

    public boolean isDelayMatching() {
        return StateInterpreter.isDelayMatching(get(FID.STATE));
    }

    public boolean isDelayedOpen() {
        return StateInterpreter.isDelayedOpen(get(FID.STATE));
    }

    public boolean isDelayedClose() {
        return StateInterpreter.isDelayedClose(get(FID.STATE));
    }

    public boolean isVolatilityInterruption() {
        // KR: 1 means Start of VI while 2 means End of VI
        return Math.equals(get(FID.RT_GEN_VAL1), 1.0);
    }

    public boolean isVolatilityInterruptionCompleted() {
        // KR: 1 means Start of VI while 2 means End of VI
        return Math.equals(get(FID.RT_GEN_VAL1), 2.0);
    }

    public boolean isShortSellRestricted() {
        // KR: 1 means that short selling is not restricted while 4 means that shortlselling is restricted
        return Math.equals(get(FID.RT_SH_SAL_RES), 4.0);
    }


    public boolean isAmOpenAuctionRandomMatched() {

        AM_OPEN_AUCTION_RANDOM_MATCHING for HK only
        Time (GMT)                           Reuters Trading Status   PERIOD_CDE (FID 3852)
        Between 0120 - 0122                  MA                       2
        Between 0120 - 0122 (Right-after MA) BL                       7

        return isValidFidSet(FID.TRADE) || isValidFidSet(FID.RT_PRIMACT_1) || Math.equals(get(FID.PERIOD_CDE), 2.0) || Math.equals(get(FID.PERIOD_CDE), 7.0);
    }

    @SpaceStorageType(storageType= StorageType.OBJECT)
    public DeltaFids getDelta() {
        return delta;
    }

    public void setDelta(DeltaFids delta) {
        this.delta = delta;
    }

    // Reuters only
    public boolean isSnapshot() {
        return isSnapshot;
    }

    public void setSnapshot() {
        this.isSnapshot = true;
    }*/
}