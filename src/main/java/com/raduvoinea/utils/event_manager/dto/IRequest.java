package com.raduvoinea.utils.event_manager.dto;

public interface IRequest<Result> extends IEvent {

    void respond(Result result);

    Result getResult();

    default void setResult(Result result) {
        respond(result);
    }

}
