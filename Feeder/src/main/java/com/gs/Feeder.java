package com.gs;

import com.gigaspaces.client.WriteModifiers;
import com.gs.model.TickData;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;

public class Feeder {
    //ToDo connect to publisher space write 10k TickData
    //ToDo update TickData at increasing rate
    final static int nobjects = 10000;

    // gigaSpace.writeMultiple(data, WriteModifiers.ONE_WAY.add(WriteModifiers.UPDATE_OR_WRITE));
    @Autowired
    @Qualifier("mdGigaSpace")
    protected GigaSpace mdGigaSpace;

    @PostConstruct
    public void run(){
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
        while (true){
            writeTicks();
            try {
                Thread.sleep(100);
            }
            catch (Exception e) {}
        }

    }
}
