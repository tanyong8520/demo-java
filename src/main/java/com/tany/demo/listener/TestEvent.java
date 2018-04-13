package com.tany.demo.listener;

import org.springframework.context.ApplicationEvent;

public class TestEvent extends ApplicationEvent {
    private String eventInfo;

    public TestEvent(Object source,String info){
        super(source);
        this.eventInfo = info;
    }

    public void setEventInfo(String info){
        this.eventInfo = info;
    }

    public String getEventInfo(){
        return this.eventInfo;
    }
}
