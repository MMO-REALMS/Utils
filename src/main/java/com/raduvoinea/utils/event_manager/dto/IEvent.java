package com.raduvoinea.utils.event_manager.dto;

public interface IEvent {

    default void fire() {
        fire(true);
    }

    void fire(boolean suppressExceptions);

}
