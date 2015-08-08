package peak.can.basic;

/**
 * Represent the PCAN error and status codes
 *
 * @version 1.0
 * @LastChange 15/12/2009
 * @author Urban Jonathan
 *
 * @Copyright (C) 1999-2009  PEAK-System Technik GmbH, Darmstadt
 * more Info at http://www.peak-system.com
 */
public enum TPCANStatus
{
    /**No Error*/
    PCAN_ERROR_OK(0x00000),
    /**Transmit buffer in CAN controller is full*/
    PCAN_ERROR_XMTFULL(0x00001),
    /**CAN controller was read too late*/
    PCAN_ERROR_OVERRUN(0x00002),
    /**Bus error: an error counter reached the 'light' limit*/
    PCAN_ERROR_BUSLIGHT(0x00004),
    /**Bus error: an error counter reached the 'heavy' limit*/
    PCAN_ERROR_BUSHEAVY(0x00008),
    /**Bus error: the CAN controller is in bus-off state*/
    PCAN_ERROR_BUSOFF(0x00010),
    /**PCAN_ERROR_ANYBUSERR*/
    PCAN_ERROR_ANYBUSERR(0x00004 | 0x00008 | 0x00010),
    /**Receive queue is empty*/
    PCAN_ERROR_QRCVEMPTY(0x00020),
    /**Receive queue was read too late*/
    PCAN_ERROR_QOVERRUN(0x00040),
    /**Transmit queue is full*/
    PCAN_ERROR_QXMTFULL(0x00080),
    /**Test of the CAN controller hardware registers failed (no hardware found)*/
    PCAN_ERROR_REGTEST(0x00100),
    /**Driver not loaded*/
    PCAN_ERROR_NODRIVER(0x00200),
    /**Resource (FIFO, Client, timeout) cannot be created*/
    PCAN_ERROR_RESOURCE(0x02000),
    /**Invalid parameter*/
    PCAN_ERROR_ILLPARAMTYPE(0x04000),
    /**Invalid parameter value*/
    PCAN_ERROR_ILLPARAMVAL(0x08000),
    /**Mask for all handle errors*/
    PCAN_ERROR_ILLHANDLE(0x01C00),
    /**Unknow error*/
    PCAN_ERROR_UNKNOWN(0x10000),
    /**Channel is not initialized*/
    PCAN_ERROR_INITIALIZE(0x40000);

    private TPCANStatus(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
    private final int value;
};
