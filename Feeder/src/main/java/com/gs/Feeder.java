package com.gs;

import com.gigaspaces.client.WriteModifiers;
import com.gs.model.TickData;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.logging.Logger;

@Component
public class Feeder {
    //ToDo connect to publisher space write 10k TickData
    //ToDo update TickData at increasing rate
    final static int nobjects = 10000;

    private Logger log = Logger.getLogger(this.getClass().getName());

    // gigaSpace.writeMultiple(data, WriteModifiers.ONE_WAY.add(WriteModifiers.UPDATE_OR_WRITE));
    @Resource
    protected GigaSpace mdGigaSpace;

    @PostConstruct
    public void run(){
        log.info("========PostConstruct run ==========================");
        writeTicks();
        updateTicks();
    }

    /*
    write nobjects
     */
    public void writeTicks(){

        for (int k=0; k<nobjects; k++ ){
            TickData tickData = new TickData();
            tickData.setId(""+k);
            tickData.setSymbol(""+k);
            mdGigaSpace.write(tickData, WriteModifiers.ONE_WAY.add(WriteModifiers.UPDATE_OR_WRITE));
        }
    }

    /*
    update at increasing rates - add more feeders use onw way for speed
     */
    public void updateTicks(){
        long counter =0l;
        while (true){
            writeTicks();
            try {
                counter++;
                log.info("update count :"+counter);
                Thread.sleep(130);
            }
            catch (Exception e) {}
        }

    }
}
