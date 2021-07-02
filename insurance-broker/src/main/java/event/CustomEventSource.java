package main.java.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomEventSource {

    private List listeners = new ArrayList();

    public synchronized void addEventListener(CustomEventListener listener)  {
        this.listeners.add(listener);
    }

    public synchronized void removeEventListener(CustomEventListener listener)   {
        this.listeners.remove(listener);
    }

    // call this method whenever you want to notify
    //the event listeners of the particular event
    private synchronized void fireEvent() {
        CustomEvent customEvent = new CustomEvent(this);
        Iterator i = listeners.iterator();
        while(i.hasNext())  {
            ((CustomEventListener) i.next()).eventHandler(customEvent);
        }
    }
}
