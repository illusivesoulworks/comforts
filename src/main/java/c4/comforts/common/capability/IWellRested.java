/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.common.capability;

public interface IWellRested {

    long getSleepTime();

    void setSleepTime(long sleepTime);

    long getWakeTime();

    void setWakeTime(long wakeTime);

    long getTiredTime();

    void setTiredTime(long tiredTime);

}
