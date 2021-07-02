package main.java.event;

import java.util.EventObject;

public class CustomEvent extends EventObject {
    //here's the constructor
    public CustomEvent(Object source) {
        super(source);
    }
}
