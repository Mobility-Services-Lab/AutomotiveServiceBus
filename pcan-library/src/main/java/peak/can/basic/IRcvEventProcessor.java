package peak.can.basic;

/**
 * This interface is implemented by classes which need to process the CAN Receive-Event.
 *
 * @version 1.0
 * @LastChange 15/12/2009
 * @author Urban Jonathan
 *
 * @Copyright (C) 1999-2009  PEAK-System Technik GmbH, Darmstadt
 * more Info at http://www.peak-system.com
 */
public interface IRcvEventProcessor
{
    /**
     * This method is called by the RcvEventDispatcher to process the CAN Receive-Event
     * by the current implementor
     */
    public void processRcvEvent(TPCANHandle channel);
}