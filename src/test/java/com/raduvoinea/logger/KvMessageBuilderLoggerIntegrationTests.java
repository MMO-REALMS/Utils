package com.raduvoinea.logger;

import com.raduvoinea.logger.dto.TestLogger;
import com.raduvoinea.utils.logger.KvLog;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.KvMessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KvMessageBuilderLoggerIntegrationTests {

    @BeforeEach
    void beforeEach() {
        Logger.reset();
        Logger.setInstance(new TestLogger(true));
    }

    private @NotNull TestLogger logger() {
        if (Logger.getInstance() instanceof TestLogger instance) {
            return instance;
        }

        fail("Logger instance is not a TestLogger");
        return null;
    }

    @Test
    void resolvedBuilderCommitsCorrectType() {
        KvLog template = new KvLog("{module}.{action}");

        new KvMessageBuilder(template)
                .parse("{module}", "auth")
                .parse("{action}", "login")
                .parse()
                .commit();

        assertEquals(1, logger().getBuffer().size());
        assertTrue(logger().getBuffer().getFirst().contains("\"__type\": \"auth.login\""));
    }

    @Test
    void resolvedBuilderCommitsCorrectStringValues() {
        KvLog template = new KvLog("player_event");
        template.add("username", "{name}");
        template.add("world", "{world}");

        new KvMessageBuilder(template)
                .parse("{name}", "Radu")
                .parse("{world}", "overworld")
                .parse()
                .commit();

        String logged = logger().getBuffer().getFirst();
        assertTrue(logged.contains("\"username\": \"Radu\""));
        assertTrue(logged.contains("\"world\": \"overworld\""));
    }

    @Test
    void nonStringValuesArePreservedAfterCommit() {
        KvLog template = new KvLog("{event}");
        template.add("count", 42);
        template.add("ratio", 3.14);
        template.add("active", true);

        new KvMessageBuilder(template)
                .parse("{event}", "tick")
                .parse()
                .commit();

        String logged = logger().getBuffer().getFirst();
        assertTrue(logged.contains("\"count\": 42"));
        assertTrue(logged.contains("\"ratio\": 3.14"));
        assertTrue(logged.contains("\"active\": true"));
    }

    @Test
    void templateReusedForMultiplePlayerEvents() {
        KvLog template = new KvLog("player.{action}");
        template.add("username", "{name}");
        template.add("ip", "{ip}");

        KvMessageBuilder templateBuilder = new KvMessageBuilder(template);

        templateBuilder.clone()
                .parse("{action}", "join")
                .parse("{name}", "Radu")
                .parse("{ip}", "192.168.1.1")
                .parse()
                .commit();

        templateBuilder.clone()
                .parse("{action}", "quit")
                .parse("{name}", "Steve")
                .parse("{ip}", "10.0.0.5")
                .parse()
                .commit();

        List<String> buffer = logger().getBuffer();
        assertEquals(2, buffer.size());

        assertTrue(buffer.getFirst().contains("\"__type\": \"player.join\""));
        assertTrue(buffer.getFirst().contains("\"username\": \"Radu\""));
        assertTrue(buffer.getFirst().contains("\"ip\": \"192.168.1.1\""));

        assertTrue(buffer.get(1).contains("\"__type\": \"player.quit\""));
        assertTrue(buffer.get(1).contains("\"username\": \"Steve\""));
        assertTrue(buffer.get(1).contains("\"ip\": \"10.0.0.5\""));
    }

    @Test
    void partiallyBoundTemplateClonedAndSpecializedPerModule() {
        KvLog template = new KvLog("{service}.{action}");
        template.add("status", "{status}");

        KvMessageBuilder serviceTemplate = new KvMessageBuilder(template)
                .parse("{service}", "payment");

        serviceTemplate.clone()
                .parse("{action}", "initiated")
                .parse("{status}", "pending")
                .parse()
                .commit();

        serviceTemplate.clone()
                .parse("{action}", "completed")
                .parse("{status}", "ok")
                .parse()
                .commit();

        serviceTemplate.clone()
                .parse("{action}", "failed")
                .parse("{status}", "error")
                .parse()
                .commit();

        List<String> buffer = logger().getBuffer();
        assertEquals(3, buffer.size());
        assertTrue(buffer.get(0).contains("\"__type\": \"payment.initiated\""));
        assertTrue(buffer.get(0).contains("\"status\": \"pending\""));
        assertTrue(buffer.get(1).contains("\"__type\": \"payment.completed\""));
        assertTrue(buffer.get(1).contains("\"status\": \"ok\""));
        assertTrue(buffer.get(2).contains("\"__type\": \"payment.failed\""));
        assertTrue(buffer.get(2).contains("\"status\": \"error\""));
    }

    @Test
    void bulkParseMapThenCommit() {
        KvLog template = new KvLog("{module}.{action}");
        template.add("target", "{player}");
        template.add("score", 100);

        new KvMessageBuilder(template)
                .parse(Map.of("{module}", "combat", "{action}", "kill", "{player}", "Notch"))
                .parse()
                .commit();

        String logged = logger().getBuffer().getFirst();
        assertTrue(logged.contains("\"__type\": \"combat.kill\""));
        assertTrue(logged.contains("\"target\": \"Notch\""));
        assertTrue(logged.contains("\"score\": 100"));
    }

    @Test
    void tradeEventTemplateProducesCorrectLog() {
        KvLog template = new KvLog("trade.{outcome}");
        template.add("trade_id", "{trade_id}");
        template.add("buyer", "{buyer}");
        template.add("seller", "{seller}");
        template.add("amount", 500);

        KvMessageBuilder tradeTemplate = new KvMessageBuilder(template);

        tradeTemplate.clone()
                .parse("{outcome}", "completed")
                .parse("{trade_id}", "TRD-001")
                .parse("{buyer}", "Radu")
                .parse("{seller}", "Steve")
                .parse()
                .commit();

        tradeTemplate.clone()
                .parse("{outcome}", "cancelled")
                .parse("{trade_id}", "TRD-002")
                .parse("{buyer}", "Alex")
                .parse("{seller}", "Notch")
                .parse()
                .commit();

        List<String> buffer = logger().getBuffer();
        assertEquals(2, buffer.size());

        assertTrue(buffer.getFirst().contains("\"__type\": \"trade.completed\""));
        assertTrue(buffer.getFirst().contains("\"trade_id\": \"TRD-001\""));
        assertTrue(buffer.getFirst().contains("\"buyer\": \"Radu\""));
        assertTrue(buffer.getFirst().contains("\"seller\": \"Steve\""));
        assertTrue(buffer.getFirst().contains("\"amount\": 500"));

        assertTrue(buffer.get(1).contains("\"__type\": \"trade.cancelled\""));
        assertTrue(buffer.get(1).contains("\"trade_id\": \"TRD-002\""));
    }

    @Test
    void unresolvedPlaceholderPassesThroughToLog() {
        KvLog template = new KvLog("{module}.{action}");
        template.add("info", "{unbound}");

        new KvMessageBuilder(template)
                .parse("{module}", "auth")
                .parse()
                .commit();

        String logged = logger().getBuffer().getFirst();
        assertTrue(logged.contains("\"__type\": \"auth.{action}\""));
        assertTrue(logged.contains("\"{unbound}\""));
    }


    @Test
    void buildingBuilderDoesNotLogUntilCommit() {
        KvLog template = new KvLog("{event}");

        KvMessageBuilder builder = new KvMessageBuilder(template)
                .parse("{event}", "something");

        builder.parse();

        assertEquals(0, logger().getBuffer().size());

        builder.parse().commit();

        assertEquals(1, logger().getBuffer().size());
    }
}