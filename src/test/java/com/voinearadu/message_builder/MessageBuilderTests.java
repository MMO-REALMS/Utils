package com.voinearadu.message_builder;


import com.voinearadu.utils.message_builder.MessageBuilder;
import com.voinearadu.utils.message_builder.MessageBuilderList;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageBuilderTests {

    @BeforeAll
    public static void init() {
        BasicConfigurator.configure();
    }

    @Test
    public void testMessageBuilder() {
        MessageBuilder builder1 = new MessageBuilder("This is a %placeholder%");
        MessageBuilder builder2 = new MessageBuilder("This is a %placeholder-1% with %placeholder-2%");

        String result1 = builder1
                .parse("placeholder", "banana")
                .parse();

        String result2 = builder2
                .parse("placeholder-1", "banana")
                .parse("%placeholder-2%", "1000 calories")
                .parse();

        assertEquals("This is a banana", result1);
        assertEquals("This is a banana with 1000 calories", result2);
    }

    @Test
    public void testMessageBuilderList() {
        MessageBuilderList builder = new MessageBuilderList(Arrays.asList(
                "This is a %placeholder-1%",
                "This %placeholder-1% has %placeholder-2%"
        ));

        List<String> result = builder
                .parse("placeholder-1", "banana")
                .parse("%placeholder-2%", "1000 calories")
                .parse();

        List<String> expected = Arrays.asList(
                "This is a banana",
                "This banana has 1000 calories"
        );
        assertArrayEquals(expected.toArray(), result.toArray());
    }

}
