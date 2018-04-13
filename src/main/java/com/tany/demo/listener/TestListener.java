package com.tany.demo.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {

    @EventListener
    public void alertEvent(TestEvent alertEvent) {

        System.out.println("处理时间消息："+alertEvent.getEventInfo());
    }

}
