package peak.can.basic;

import java.util.Stack;
import java.util.Vector;

/**
 * Represents a PCAN-hardware channel handle.
 *
 * @version 1.0
 * @LastChange 15/12/2009
 * @author Urban Jonathan
 *
 * @Copyright (C) 1999-2009  PEAK-System Technik GmbH, Darmstadt
 * more Info at http://www.peak-system.com
 */
public enum TPCANHandle
{

    /**Undefined/default value for a PCAN bus*/
    PCAN_NONEBUS((byte) 0x00),
    /**PCAN-ISA interface, channel 1*/
    PCAN_ISABUS1((byte) 0x21),
    /**PCAN-ISA interface, channel 2*/
    PCAN_ISABUS2((byte) 0x22),
    /**PCAN-ISA interface, channel 3*/
    PCAN_ISABUS3((byte) 0x23),
    /**PCAN-ISA interface, channel 4*/
    PCAN_ISABUS4((byte) 0x24),
    /**PCAN-ISA interface, channel 5*/
    PCAN_ISABUS5((byte) 0x25),
    /**PCAN-ISA interface, channel 6*/
    PCAN_ISABUS6((byte) 0x26),
    /**PCAN-ISA interface, channel 7*/
    PCAN_ISABUS7((byte) 0x27),
    /**PCAN-ISA interface, channel 8*/
    PCAN_ISABUS8((byte) 0x28),
    /**PCAN-Dongle/LPT interface, channel 1*/
    PCAN_DNGBUS1((byte) 0x31),
    /**PCAN-PCI interface, channel 1*/
    PCAN_PCIBUS1((byte) 0x41),
    /**PCAN-PCI interface, channel 2*/
    PCAN_PCIBUS2((byte) 0x42),
    /**PCAN-PCI interface, channel 3*/
    PCAN_PCIBUS3((byte) 0x43),
    /**PCAN-PCI interface, channel 4*/
    PCAN_PCIBUS4((byte) 0x44),
    /**PCAN-PCI interface, channel 5*/
    PCAN_PCIBUS5((byte) 0x45),
    /**PCAN-PCI interface, channel 6*/
    PCAN_PCIBUS6((byte) 0x46),
    /**PCAN-PCI interface, channel 7*/
    PCAN_PCIBUS7((byte) 0x47),
    /**PCAN-PCI interface, channel 8*/
    PCAN_PCIBUS8((byte) 0x48),
    /**PCAN-PCI interface, channel 1*/
    PCAN_USBBUS1((byte) 0x51),
    /**PCAN-USB interface, channel 2*/
    PCAN_USBBUS2((byte) 0x52),
    /**PCAN-USB interface, channel 3*/
    PCAN_USBBUS3((byte) 0x53),
    /**PCAN-USB interface, channel 4*/
    PCAN_USBBUS4((byte) 0x54),
    /**PCAN-USB interface, channel 5*/
    PCAN_USBBUS5((byte) 0x55),
    /**PCAN-USB interface, channel 6*/
    PCAN_USBBUS6((byte) 0x56),
    /**PCAN-USB interface, channel 7*/
    PCAN_USBBUS7((byte) 0x57),
    /**PCAN-USB interface, channel 8*/
    PCAN_USBBUS8((byte) 0x58),
    /**PCAN-PC Card interface, channel 1*/
    PCAN_PCCBUS1((byte) 0x61),
    /**PCAN-PC Card interface, channel 2*/
    PCAN_PCCBUS2((byte) 0x62);

    private TPCANHandle(byte value)
    {
        this.value = value;
    }

    public byte getValue()
    {
        return this.value;
    }

    /**
     * Returns All PCAN Channels which are initializable (All except PCAN_NONEBUS)
     * @return PCAN Channels array
     */
    public static Object[] initializableChannels()
    {
        Vector<TPCANHandle> result = new Stack<TPCANHandle>();
        for (int i = 1; i < TPCANHandle.values().length; i++)
        {
            result.add(TPCANHandle.values()[i]);
        }
        return result.toArray();
    }

    /**
     * Verify the provided TPCANHandle is an USB Device
     * @param handle to verify
     * @return true if the TPCANHandle is an USB Device, false if not
     */
    public static boolean isPCANUSBHardware(TPCANHandle handle)
    {
        switch (handle)
        {
            case PCAN_USBBUS1:
            case PCAN_USBBUS2:
            case PCAN_USBBUS3:
            case PCAN_USBBUS4:
            case PCAN_USBBUS5:
            case PCAN_USBBUS6:
            case PCAN_USBBUS7:
            case PCAN_USBBUS8:
                return true;
            default:
                return false;
        }
    }

    /**
     * Verify the provided TPCANHandle is an PC-Card Device
     * @param handle to verify
     * @return true if the TPCANHandle is an PC-Card Device, false if not
     */
    public static boolean isPCANPCCardHardware(TPCANHandle handle)
    {
        switch (handle)
        {
            case PCAN_PCCBUS1:
            case PCAN_PCCBUS2:
                return true;
            default:
                return false;
        }
    }

    /**
     * Verify the provided TPCANHandle contains a SJA1000 controller
     * @param handle to verify
     * @return true if the TPCANHandle contains a SJA1000 controller, false if not
     */
    public static boolean containsSJA(TPCANHandle handle, TPCANType type)
    {
        switch (handle)
        {
            case PCAN_DNGBUS1:
            case PCAN_ISABUS1:
            case PCAN_ISABUS2:
            case PCAN_ISABUS3:
            case PCAN_ISABUS4:
            case PCAN_ISABUS5:
            case PCAN_ISABUS6:
            case PCAN_ISABUS7:
            case PCAN_ISABUS8:
                switch (type)
                {
                    case PCAN_TYPE_DNG_SJA:
                    case PCAN_TYPE_DNG_SJA_EPP:
                    case PCAN_TYPE_ISA_SJA:
                        return true;
                }
        }
        return false;
    }
    private final byte value;
};
