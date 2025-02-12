package com.voinearadu.event_manager.dto;

import com.voinearadu.event_manager.EventManagerTests;
import com.voinearadu.utils.event_manager.dto.LocalEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalEvent {

    private int number1;
    private int number2;
    private int result;

    public ExternalEvent(int number1, int number2) {
        this.number1 = number1;
        this.number2 = number2;
        this.result = number1 + number2;
    }

    @Getter
    public static class Wrapper extends LocalEvent {
        private ExternalEvent event;

        public Wrapper(ExternalEvent event) {
            super(EventManagerTests.getEventManager());
            this.event = event;
        }
    }

}
