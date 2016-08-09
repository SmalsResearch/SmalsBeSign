package be.smals.research.bulksign.desktopapp.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cea on 09/08/2016.
 */
public class Messages {

    public enum MessageType {
        ERROR, SUCCESS, INFO
    }

    private static Messages instance = new Messages();
    private Map<MessageType, List<String>> messages;

    private Messages () {}
    public static Messages getInstance() {
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
