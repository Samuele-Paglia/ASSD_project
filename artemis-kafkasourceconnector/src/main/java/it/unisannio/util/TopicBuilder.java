package it.unisannio.util;

public class TopicBuilder {

    public static String validateTopicName(String topicName) {
        return topicName.replace(" ", "_")
                .replace("'", "_")
                .replace("é", "e")
                .replace("è", "e")
                .replace("à", "a")
                .replace("ô", "o")
                .replace("É", "E")
                .replace("À", "A");
    }
}
