package com.voinearadu.utils.redis_manager.event;

import com.voinearadu.utils.event_manager.dto.IEvent;
import com.voinearadu.utils.lambda.ScheduleUtils;
import com.voinearadu.utils.lambda.lambda.ArgLambdaExecutor;
import com.voinearadu.utils.lambda.lambda.LambdaExecutor;
import com.voinearadu.utils.redis_manager.dto.RedisResponse;
import com.voinearadu.utils.redis_manager.event.impl.ResponseEvent;
import com.voinearadu.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

@Getter
public class RedisRequest<Response> implements IEvent {

    private final String className;
    protected transient @Setter RedisManager redisManager;
    private @Setter long id = -1;
    private @Setter String originator = "UNKNOWN";
    private String target;

    public RedisRequest(RedisManager redisManager, String className, long id, String originator, String target) {
        this.redisManager = redisManager;
        this.className = className;
        this.id = id;
        this.originator = originator;
        setTarget(target);
    }

    public RedisRequest(RedisManager redisManager, String target) {
        this.redisManager = redisManager;
        this.className = getClass().getName();
        setTarget(target);
    }

    public static @Nullable RedisRequest<?> deserialize(RedisManager redisManager, String data) {
        RedisRequest<?> event = redisManager.getGson().execute().fromJson(data, RedisRequest.class);

        if(event == null) {
            return null;
        }

        event.setRedisManager(redisManager);
        return event;
    }

    public void setTarget(String target) {
        if (target.contains("#")) {
            this.target = target;
        } else {
            this.target = redisManager.getRedisConfig().getChannelBase() + "#" + target;
        }
    }

    /**
     * Sends the event locally only
     * Do NOT call manually, call {@link #sendAndWait()} or any of it derivatives
     */
    public void fireEvent() {
        redisManager.getEventManager().fire(this);
    }

    @Override
    public String toString() {
        return redisManager.getGson().execute().toJson(this);
    }

    @SuppressWarnings("unused")
    public void respond(Response response) {
        new ResponseEvent(redisManager, this, response).send();
    }

    @SuppressWarnings("UnusedReturnValue")
    public RedisResponse<Response> send() {
        return redisManager.send(this);
    }

    public void sendAndExecuteSync(ArgLambdaExecutor<Response> success, LambdaExecutor fail) {
        RedisResponse<Response> response = this.sendAndWait();

        if (response.hasTimeout()) {
            fail.execute();
            return;
        }

        success.execute(response.getResponse());
    }

    public @Nullable Response sendAndGet(LambdaExecutor fail) {
        RedisResponse<Response> response = this.sendAndWait();

        if (response.hasTimeout()) {
            fail.execute();
            return null;
        }

        return response.getResponse();
    }

    @SuppressWarnings("unused")
    public @Nullable Response sendAndGet() {
        return sendAndGet(() -> {
        });
    }

    @SuppressWarnings("unused")
    public void sendAndExecute(ArgLambdaExecutor<Response> success) {
        sendAndExecute(success, () -> {
        });
    }

    public void sendAndExecute(ArgLambdaExecutor<Response> success, LambdaExecutor fail) {
        ScheduleUtils.runTaskAsync(() -> sendAndExecuteSync(success, fail));
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    @SneakyThrows
    public RedisResponse<Response> sendAndWait() {
        return sendAndWait(redisManager.getRedisConfig().getTimeout());
    }

    @SuppressWarnings("BusyWait")
    @SneakyThrows
    public RedisResponse<Response> sendAndWait(int timeout) {
        int currentWait = 0;
        RedisResponse<Response> response = send();
        while (!response.isFinished()) {
            Thread.sleep(redisManager.getRedisConfig().getWaitBeforeIteration());
            currentWait += redisManager.getRedisConfig().getWaitBeforeIteration();
            if (currentWait > timeout) {
                response.timeout();
                break;
            }
        }

        redisManager.getAwaitingResponses().remove(response);
        return response;
    }

    @SuppressWarnings("unused")
    public String getRedisTargetID() {
        String[] split = target.split("#");
        return split[split.length - 1];
    }

    @SuppressWarnings("unused")
    public String getOriginatorID() {
        String[] split = originator.split("#");
        return split[split.length - 1];
    }

}
