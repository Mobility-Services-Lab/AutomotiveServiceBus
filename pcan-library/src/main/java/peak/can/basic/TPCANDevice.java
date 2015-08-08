package peak.can.basic;

/**
 * Represents a PCAN device
 *
 * @version 1.0
 * @LastChange 15/12/2009
 * @author Urban Jonathan
 *
 * @Copyright (C) 1999-2009  PEAK-System Technik GmbH, Darmstadt
 * more Info at http://www.peak-system.com
 */
public enum TPCANDevice
{
    /**Undefined, unknown or not selected PCAN device value*/
    PCAN_NONE((byte)0x00),
    /**PCAN Non-Plug&Play devices. NOT USED WITHIN PCAN-Basic API*/
    PCAN_PEAKCAN((byte)0x01),
    /**PCAN-ISA, PCAN-PC/104, and PCAN-PC/104-Plus*/
    PCAN_ISA((byte)0x02),
    /**PCAN-Dongle*/
    PCAN_DNG((byte)0x03),
    /**PCAN-PCI, PCAN-cPCI, PCAN-miniPCI, and PCAN-PCI Express*/
    PCAN_PCI((byte)0x04),
    /**PCAN-USB and PCAN-USB Pro*/
    PCAN_USB((byte)0x05),
    /**PCAN-PC Card*/
    PCAN_PCC((byte)0x06);

    private TPCANDevice(byte value)
    {
        this.value = value;
    }

    public byte getValue()
    {
        return this.value;
    }
    private final byte value;
};
