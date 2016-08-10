package be.smals.research.bulksign.desktopapp.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cea on 09/08/2016.
 */
public class Message {

    public enum MessageType {
        ERROR, SUCCESS, INFO
    }

    private static Message instance = new Message();
    private Map<MessageType, List<String>> messages;

    private Message() {}
    public static Message getInstance() {
        return instance;
    }

    public Map getMessages () {
        return this.messages;
    }
    public void addMessage (MessageType type, String message) {
        this.messages.putIfAbsent(type, new ArrayList<>());
        this.messages.get(type).add(message);
    }
    public List getMessages (MessageType type) {
        return this.messages.get(type);
    }
}
