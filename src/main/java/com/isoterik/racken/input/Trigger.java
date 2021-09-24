package com.isoterik.racken.input;

/**
 * The base class for all triggers.
 * @see Trigger
 * @see KeyTrigger
 * @see TouchTrigger
 * @see GestureTrigger
 *
 * @author imranabdulmalik
 */
public abstract class Trigger implements ITrigger {
    protected boolean active = true;
    protected boolean polled = false;
    
    public Trigger setActive (boolean active)
    { this.active = active; return this; }

    public boolean isActive ()
    { return active; }

    public Trigger setPolled (boolean polled)
    { this.polled = polled; return this; }

    public boolean isPolled ()
    { return polled; }
}
