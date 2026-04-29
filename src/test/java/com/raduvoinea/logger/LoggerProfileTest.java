package com.raduvoinea.logger;

import com.raduvoinea.benchmark.Profiler;
import com.raduvoinea.logger.dto.PrintAsJsonTestObject;
import com.raduvoinea.logger.dto.TestLogger;
import com.raduvoinea.utils.logger.KvLog;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.KVMessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoggerProfileTest {

    private static final int ITERATIONS = 100_000;

    private TestLogger testLogger;

    @BeforeEach
    void setup() {
        Logger.reset();
        testLogger = new TestLogger(false);
        Logger.setInstance(testLogger);
    }

    @AfterEach
    void teardown() {
        testLogger.getBuffer().clear();
    }

    private void clearBuffer() {
        testLogger.getBuffer().clear();
    }

    @Test
    void profile_messageBuilderWithLogger() {
        MessageBuilder template = new MessageBuilder("Player {name} performed {action} in {world} with score {score}");
        MessageBuilderList listTemplate = new MessageBuilderList(Arrays.asList(
                "Player {name} joined {world}",
                "{name} has score {score}",
                "{name} performed {action}"
        ));

        Profiler.suite(ITERATIONS)
                .run("MessageBuilder — parse + Logger.log",
                        () -> {
                            clearBuffer();
                            String result = template.clone()
                                    .parse("{name}", "Radu")
                                    .parse("{action}", "login")
                                    .parse("{world}", "overworld")
                                    .parse("{score}", "9999")
                                    .parse();
                            Logger.log(result);
                        })

                .run("MessageBuilderList — parse + Logger.log each line",
                        () -> {
                            clearBuffer();
                            List<String> lines = listTemplate.clone()
                                    .parse("{name}", "Radu")
                                    .parse("{action}", "kill")
                                    .parse("{world}", "nether")
                                    .parse("{score}", "500")
                                    .parse();
                            for (String line : lines) {
                                Logger.log(line);
                            }
                        })

                .print();
    }

    @Test
    void profile_kvMessageBuilderWithLogger() {
        KvLog base = new KvLog("player.{action}");
        base.add("username", "{name}");
        base.add("world", "{world}");
        base.add("score", 9000);
        KVMessageBuilder template = new KVMessageBuilder(base);

        Profiler.suite(ITERATIONS)
                .run("KVMessageBuilder — clone + parse + commit",
                        () -> {
                            clearBuffer();
                            template.clone()
                                    .parse("{action}", "login")
                                    .parse("{name}", "Radu")
                                    .parse("{world}", "overworld")
                                    .parse()
                                    .commit();
                        })

                .run("KVMessageBuilder — high frequency clone + commit", ITERATIONS / 100, () -> {
                    for (int i = 0; i < 100; i++) {
                        clearBuffer();
                        template.clone()
                                .parse("{action}", i % 2 == 0 ? "join" : "quit")
                                .parse("{name}", "Player" + i)
                                .parse("{world}", "world_" + (i % 3))
                                .parse()
                                .commit();
                    }
                })

                .print();
    }

    @Test
    void profile_kvDirectVsBuilderVsLogAsJson() {
        KvLog base = new KvLog("player.{action}");
        base.add("username", "{name}");
        base.add("world", "{world}");
        base.add("score", 9000);
        KVMessageBuilder kvTemplate = new KVMessageBuilder(base);

        MessageBuilder msgTemplate = new MessageBuilder(
                "player.join | username={name} | world={world} | score=9000"
        );

        Profiler.suite(ITERATIONS)
                .run("MessageBuilder — clone + parse + log",
                        () -> {
                            clearBuffer();
                            Logger.log(msgTemplate.clone()
                                    .parse("{name}", "Radu")
                                    .parse("{world}", "overworld")
                                    .parse("{action}", "join")
                                    .parse());
                        })

                .run("KVMessageBuilder — clone + parse + commit",
                        () -> {
                            clearBuffer();
                            kvTemplate.clone()
                                    .parse("{action}", "join")
                                    .parse("{name}", "Radu")
                                    .parse("{world}", "overworld")
                                    .parse()
                                    .commit();
                        })

                .run("Logger.kv() — direct",
                        () -> {
                            clearBuffer();
                            Logger.kv("player.join")
                                    .add("username", "Radu")
                                    .add("world", "overworld")
                                    .add("score", 9000)
                                    .commit();
                        })

                .run("@LogAsJson — new instance per call",
                        () -> {
                            clearBuffer();
                            Logger.debug(new PrintAsJsonTestObject(
                                    "Radu", 9000,
                                    new HashMap<>() {{ put("world", "overworld"); }},
                                    List.of("join")
                            ));
                        })

                .run("Logger.kv() — nested",
                        () -> {
                            clearBuffer();
                            Logger.kv("trade_event")
                                    .add("trade_id", "TRD-001")
                                    .add("buyer", Logger.kv("player").add("username", "Radu").add("balance", 1000))
                                    .add("seller", Logger.kv("player").add("username", "Steve").add("balance", 500))
                                    .add("amount", 250)
                                    .commit();
                        })

                .run("@LogAsJson — large object",
                        () -> {
                            clearBuffer();
                            Logger.debug(new PrintAsJsonTestObject(
                                    "a-longer-string-value",
                                    999999,
                                    new HashMap<>() {{
                                        put("key1", "value1"); put("key2", "value2");
                                        put("key3", "value3"); put("key4", "value4");
                                    }},
                                    List.of("item1", "item2", "item3", "item4", "item5")
                            ));
                        })

                .print();
    }
}