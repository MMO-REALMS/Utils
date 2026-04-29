package com.raduvoinea.message_builder;

import com.raduvoinea.benchmark.Profiler;
import com.raduvoinea.utils.logger.KvLog;
import com.raduvoinea.utils.message_builder.KVMessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

public class MessageBuilderProfileTest {

    private static final int ITERATIONS = 100_000;

    @Test
    void profile_messageBuilder() {
        MessageBuilder multiTemplate = new MessageBuilder("{module}.{action} triggered by {player} in {world}");
        MessageBuilder mapTemplate = new MessageBuilder("Event: {event}, Player: {player}, World: {world}, Score: {score}");
        MessageBuilder chainTemplate = new MessageBuilder("{p1}")
                .parse("p1", "{p2}").parse("p2", "{p3}").parse("p3", "{p4}").parse("p4", "resolved");

        Profiler.suite(ITERATIONS)
                .run("MessageBuilder — single placeholder",
                        () -> new MessageBuilder("Hello {name}!")
                                .parse("{name}", "Radu")
                                .parse())

                .run("MessageBuilder — multiple placeholders",
                        () -> multiTemplate.clone()
                                .parse("{module}", "auth")
                                .parse("{action}", "login")
                                .parse("{player}", "Radu")
                                .parse("{world}", "overworld")
                                .parse())

                .run("MessageBuilder — bulk parse(Map)",
                        () -> mapTemplate.clone()
                                .parse(Map.of(
                                        "{event}", "player_join",
                                        "{player}", "Radu",
                                        "{world}", "overworld",
                                        "{score}", "9000"))
                                .parse())

                .run("MessageBuilder — deep chained placeholders",
                        chainTemplate::parse)

                .run("MessageBuilder — clone then parse",
                        () -> mapTemplate.clone()
                                .parse("{event}", "click")
                                .parse("{player}", "Steve")
                                .parse("{world}", "nether")
                                .parse("{score}", "100")
                                .parse())

                .print();
    }

    @Test
    void profile_messageBuilderList() {
        MessageBuilderList shortTemplate = new MessageBuilderList(Arrays.asList(
                "Player {name} joined {world}",
                "Welcome back, {name}!"
        ));

        MessageBuilderList longTemplate = new MessageBuilderList(Arrays.asList(
                "{name} connected from {ip}",
                "{name} joined world {world}",
                "{name} has {score} points",
                "{name} is rank {rank}",
                "{name} last seen {last_seen}",
                "{name} has {friends} friends online",
                "{name} is a {role} in guild {guild}",
                "{name} completed quest {quest}"
        ));

        Profiler.suite(ITERATIONS)
                .run("MessageBuilderList — short list",
                        () -> shortTemplate.clone()
                                .parse("{name}", "Radu")
                                .parse("{world}", "overworld")
                                .parse())

                .run("MessageBuilderList — long list (8 lines, 10 placeholders)",
                        () -> longTemplate.clone()
                                .parse("{name}", "Radu")
                                .parse("{ip}", "192.168.1.1")
                                .parse("{world}", "overworld")
                                .parse("{score}", "9999")
                                .parse("{rank}", "1")
                                .parse("{last_seen}", "2024-01-15")
                                .parse("{friends}", "5")
                                .parse("{role}", "TANK")
                                .parse("{guild}", "Blackrock")
                                .parse("{quest}", "Dragon's Lair")
                                .parse())

                .run("MessageBuilderList — bulk parse(Map)",
                        () -> shortTemplate.clone()
                                .parse(Map.of("{name}", "Radu", "{world}", "overworld"))
                                .parse())

                .print();
    }

    @Test
    void profile_kvMessageBuilder() {
        KvLog multiBase = new KvLog("{module}.{action}");
        multiBase.add("player", "{name}");
        multiBase.add("world", "{world}");

        KvLog nonStringBase = new KvLog("tick_event");
        nonStringBase.add("count", 42);
        nonStringBase.add("ratio", 3.14);
        nonStringBase.add("active", true);
        nonStringBase.add("label", "{label}");

        KvLog cloneBase = new KvLog("player.{action}");
        cloneBase.add("username", "{name}");
        cloneBase.add("ip", "{ip}");
        cloneBase.add("ping", 42);
        KVMessageBuilder cloneTemplate = new KVMessageBuilder(cloneBase);

        Profiler.suite(ITERATIONS)
                .run("KVMessageBuilder — single placeholder",
                        () -> new KVMessageBuilder(new KvLog("{event}"))
                                .parse("{event}", "player_join")
                                .parse())

                .run("KVMessageBuilder — multiple placeholders",
                        () -> new KVMessageBuilder(multiBase)
                                .parse("{module}", "auth")
                                .parse("{action}", "login")
                                .parse("{name}", "Radu")
                                .parse("{world}", "overworld")
                                .parse())

                .run("KVMessageBuilder — with non-string values",
                        () -> new KVMessageBuilder(nonStringBase)
                                .parse("{label}", "active-session")
                                .parse())

                .run("KVMessageBuilder — clone then parse",
                        () -> cloneTemplate.clone()
                                .parse("{action}", "join")
                                .parse("{name}", "Radu")
                                .parse("{ip}", "192.168.1.1")
                                .parse())

                .run("KVMessageBuilder — clone high frequency reuse", ITERATIONS / 100, () -> {
                    for (int i = 0; i < 100; i++) {
                        cloneTemplate.clone()
                                .parse("{action}", i % 2 == 0 ? "join" : "quit")
                                .parse("{name}", "Player" + i)
                                .parse("{ip}", "10.0.0." + (i % 255))
                                .parse();
                    }
                })

                .print();
    }
}