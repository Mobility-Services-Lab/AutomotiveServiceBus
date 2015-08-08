package peak.can.basic;
/**
 * Baud rate codes = BTR0/BTR1 register values for the CAN controller.
 * You can define your own Baud rate with the BTROBTR1 register.
 * Take a look at www.peak-system.com for our free software "BAUDTOOL"
 * to calculate the BTROBTR1 register for every baudrate and sample point.
 *
 * @version 1.0
 * @LastChange 15/12/2009
 * @author Urban Jonathan
 *
 * @Copyright (C) 1999-2009  PEAK-System Technik GmbH, Darmstadt
 * more Info at http://www.peak-system.com
 */
public enum TPCANBaudrate
{
    /**1 MBit/s*/
    PCAN_BAUD_1M(0x0014),
    /**500 kBit/s*/
    PCAN_BAUD_500K(0x001C),
    /**250 kBit/s*/
    PCAN_BAUD_250K(0x011C),
    /**125 kBit/s*/
    PCAN_BAUD_125K(0x031C),
    /**100 kBit/s*/
    PCAN_BAUD_100K(0x432F),
    /**50 kBit/s*/
    PCAN_BAUD_50K(0x472F),
    /**20 kBit/s*/
    PCAN_BAUD_20K(0x532F),
    /**10 kBit/s*/
    PCAN_BAUD_10K(0x672F),
    /**5 kBit/s*/
    PCAN_BAUD_5K(0x7F7F);

    private TPCANBaudrate(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
    private final int value;
};
