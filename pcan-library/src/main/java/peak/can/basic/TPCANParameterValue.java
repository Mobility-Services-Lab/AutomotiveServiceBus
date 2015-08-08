package peak.can.basic;

/**
 * Parameter values definition
 *
 * @version 1.0
 * @LastChange 15/12/2009
 * @author Urban Jonathan
 *
 * @Copyright (C) 1999-2009  PEAK-System Technik GmbH, Darmstadt
 * more Info at http://www.peak-system.com
 */
public enum TPCANParameterValue
{
    /** The PCAN parameter is not set (inactive)*/
    PCAN_PARAMETER_OFF(0),
    /** The PCAN parameter is set (active)*/
    PCAN_PARAMETER_ON(1),
    /** The PCAN filter is closed. No messages will be received*/
    PCAN_FILTER_CLOSE(0),
    /** The PCAN filter is fully opened. All messages will be received*/
    PCAN_FILTER_OPEN(1),
    /** The PCAN filter is custom configured. Only registered messages will be received*/
    PCAN_FILTER_CUSTOM(2),
    /** The PCAN-Channel handle is illegal*/
    PCAN_CHANNEL_ILLEGAL(0),
    /** The PCAN-Channel handle is valid to connect/initialize*/
    PCAN_CHANNEL_AVAILABLE(1),
    /** The PCAN-Channel handle is valid, but is already being used*/
    PCAN_CHANNEL_OCCUPIED(2),
    /** The PCAN-Channel handle is valid and ready for use (Plug & Play hardware only)*/
    PCAN_CHANNEL_UNOCCUPIED(3),
    /** Logs system exceptions / errors*/
    LOG_FUNCTION_DEFAULT(0x00),
    /** Logs the entries to the PCAN-Basic API functions*/
    LOG_FUNCTION_ENTRY(0x01),
    /** Logs the parameters passed to the PCAN-Basic API functions*/
    LOG_FUNCTION_PARAMETERS(0x02),
    /** Logs the exits from the PCAN-Basic API functions*/
    LOG_FUNCTION_LEAVE(0x04),
    /** Logs the CAN messages passed to the CAN_Write function*/
    LOG_FUNCTION_WRITE(0x08),
    /** Logs the CAN messages received within the CAN_Read function*/
    LOG_FUNCTION_READ(0x10);

    private TPCANParameterValue(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    /**
     * Parses a int value into the TPCANParameterValue PCAN_PARAMETER_ON or PCAN_PARAMETER_OFF
     * @param value parsed value
     * @return Corresponding TPCANParameterValue
     */
    static public TPCANParameterValue parseOnOff(int value)
    {
        if(value == TPCANParameterValue.PCAN_PARAMETER_ON.getValue())
            return TPCANParameterValue.PCAN_PARAMETER_ON;
        else if(value == TPCANParameterValue.PCAN_PARAMETER_OFF.getValue())
            return TPCANParameterValue.PCAN_PARAMETER_OFF;
        else
            return null;
    }
     /**
     * Parses a int value into the TPCANParameterValue PCAN_CHANNEL_AVAILABLE, PCAN_CHANNEL_ILLEGAL, PCAN_CHANNEL_OCCUPIED or PCAN_CHANNEL_UNOCCUPIED
     * @param value parsed value
     * @return Corresponding TPCANParameterValue
     */
    static public TPCANParameterValue parseCondition(int value)
    {
        if(value == TPCANParameterValue.PCAN_CHANNEL_AVAILABLE.getValue())
            return TPCANParameterValue.PCAN_CHANNEL_AVAILABLE;
        else if(value == TPCANParameterValue.PCAN_CHANNEL_ILLEGAL.getValue())
            return TPCANParameterValue.PCAN_CHANNEL_ILLEGAL;
        else if(value == TPCANParameterValue.PCAN_CHANNEL_OCCUPIED.getValue())
            return TPCANParameterValue.PCAN_CHANNEL_OCCUPIED;
        else if(value == TPCANParameterValue.PCAN_CHANNEL_UNOCCUPIED.getValue())
            return TPCANParameterValue.PCAN_CHANNEL_UNOCCUPIED;
        else
            return null;
    }
    /**
     * Parses a int value into the TPCANParameterValue PCAN_FILTER_CLOSE, PCAN_FILTER_OPEN or PCAN_FILTER_CUSTOM.
     * @param value parsed value
     * @return Corresponding TPCANParameterValue
     */
    static public TPCANParameterValue parseFilterStatus(int value)
    {
        if(value == TPCANParameterValue.PCAN_FILTER_CLOSE.getValue())
            return TPCANParameterValue.PCAN_FILTER_CLOSE;
        else if(value == TPCANParameterValue.PCAN_FILTER_OPEN.getValue())
            return TPCANParameterValue.PCAN_FILTER_OPEN;
        else if(value == TPCANParameterValue.PCAN_FILTER_CUSTOM.getValue())
            return TPCANParameterValue.PCAN_FILTER_CUSTOM;
        else
            return null;
    }
    private final int value;
};
