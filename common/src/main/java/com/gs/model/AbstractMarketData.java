package com.gs.model;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;


import com.gigaspaces.annotation.pojo.FifoSupport;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.annotation.pojo.SpaceStorageType;
import com.gigaspaces.metadata.StorageType;
import com.gigaspaces.metadata.index.SpaceIndexType;

@SpaceClass(persist=false, fifoSupport=FifoSupport.ALL, replicate=false, storageType=StorageType.OBJECT)
public abstract class AbstractMarketData implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Logger log = Logger.getLogger(getClass().getSimpleName());

    protected String   id;
    protected String   symbol;
    protected Source   source;
    protected Integer  bucket;
    protected Integer  level;
    //protected FidData  data;
    protected Date     timestamp;
    protected Long     startProcessTime;
    protected Integer  coreInstance;
    public static String LEVEL2 = "LEVEL2";

    public static enum Source {
        ACTIV, BBG, ALLSPARK, RT
    };

   /* public static final FID[] LOCAL_TIME_TAGS = {
            FID.TRADE_TIME,
            FID.BID_TIME,
            FID.ASK_TIME,
            FID.MARKET_PRICE_TIME
    };*/

    public AbstractMarketData() {
    }

    protected AbstractMarketData(String symbol, Source source, int cores, int level,
                                 /*FidData data,*/ Date timestamp, Long startProcessTime) {
       // this.data = data;
        this.symbol = symbol;
        this.source = source;
        this.id = getSymbolId(symbol, source);
        this.bucket = getBucket(symbol, cores);
        this.level = level;
        this.timestamp = timestamp;
        this.startProcessTime = startProcessTime;
    }

  //  public abstract void setImageStatus(boolean status);

    private int getBucket(String s, int cores){
        int hashCode = s.hashCode();
        hashCode = hashCode == Integer.MIN_VALUE ? Integer.MAX_VALUE : java.lang.Math.abs(hashCode);
        return hashCode % cores;
    }

   /* protected boolean setMap() {// returns true if the map was created
        if (data == null) {
            data = new FidData();
            return true;
        }
        return false;
    }

    public boolean isFidSet(FID fid) {
        return data.containsKey(fid);
    }

    public boolean isValidFidSet(FID fid) {
        return data.containsKey(fid) && data.get(fid)!=null;
    }

    public boolean isEmpty() {
        return (data != null)
                && (data.isEmpty() || (data.size() == 1 && isFidSet(FID.SEQUENCE_NUMBER)));
    }

    public Double get(FID fid) {
        return data.get(fid);
    }

    protected void put(FID fid, Double value) {
        if (value == null) {
            data.put(fid, null);
        } else {
            try {
                data.put(fid, Number.getNumberDouble(value.doubleValue()));
            } catch (NumberException ne) {
                log.error("Failed to update fid [{}] - value [{}] : " + ne.getMessage(),
                        fid.name(), value);
            }
        }
    }

    public Double remove(FID fid) {
        return data.remove(fid);
    }*/

    public static String getSymbolId(String symbol, Source source) {
        return symbol + ":" + source.name();
    }

    public static String getSymbolFromId(String symbolId) {
        try{return symbolId.split(":")[0];}
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return "";
    }

    public void setId(String id) {
        this.id = id;
    }

    @SpaceId(autoGenerate = false)
    @SpaceRouting
    @SpaceIndex(type = SpaceIndexType.EQUAL)
    public String getId() {
        return id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @SpaceIndex(type = SpaceIndexType.EQUAL)
    public String getSymbol() {
        return symbol;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }


    public void setBucket(Integer bucket) {
        this.bucket = bucket;
    }


    public Integer getBucket() {
        return bucket;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

   /* public void setData(FidData data) {
        this.data = data;
    }

    @SpaceStorageType(storageType= StorageType.OBJECT)
    public FidData getData() {
        return data;
    }*/

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setStartProcessTime(Long startProcessTime) {
        this.startProcessTime = startProcessTime;
    }

    public Long getStartProcessTime() {
        return startProcessTime;
    }

    public Integer getCoreInstance() {
        return coreInstance;
    }

    public void setCoreInstance(Integer coreInstance) {
        this.coreInstance = coreInstance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("symbol=").append(symbol)
                .append(" source=").append(source)
                .append(" id=").append(id)
                .append(" timeStamp=").append(getTimestamp())
                .append(" level=").append(level)
                //.append(" data=").append(data)
                .append(" coreInstance=").append(coreInstance);
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("symbol=").append(symbol)
                .append(" source=").append(source)
                .append(" level=").append(level);
               // .append(" data=").append(data);
    }

   /* public Double getAsk(int i) {
        return get(FID.getAskFID(i));
    }

    public Double getBid(int i) {
        return get(FID.getBidFID(i));
    }

    public Double getAskSize(int i) {
        return get(FID.getAskSizeFID(i));
    }

    public Double getBidSize(int i) {
        return get(FID.getBidSizeFID(i));
    }

    protected boolean checkContainNull(FID... fids) {
        for (FID fid : fids) {
            if (!isFidSet(fid) || get(fid) != null) {
                return false;
            }
        }
        return true;
    }*/

}
