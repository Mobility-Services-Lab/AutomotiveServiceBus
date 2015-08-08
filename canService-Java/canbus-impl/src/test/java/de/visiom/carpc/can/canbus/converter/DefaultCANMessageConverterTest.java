/*
 * Copyright 2015 Technische Universität München
 *
 * Author:
 * David Soto Setzke
 *
 *
 * This file is part of the Automotive Service Bus v1.1 which was
 * forked from the research project Visio.M:
 *
 * 	 http://www.visiom-automobile.de/home/
 *
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.visiom.carpc.can.canbus.converter;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.visiom.carpc.can.MessageLoader;
import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.entities.Message.SenderType;
import de.visiom.carpc.can.entities.ParameterReference;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.impl.canbus.converter.DefaultCANMessageConverter;
import de.visiom.carpc.can.impl.canbus.converter.exceptions.UnknownIdentifierException;

public class DefaultCANMessageConverterTest {

    private DefaultCANMessageConverter converter;

    private final Map<String, Signal> namesToSignals = new HashMap<String, Signal>();

    @Before
    public void setUp() {
        converter = new DefaultCANMessageConverter();
        converter.setMessageLoader(new MessageLoader() {
            @Override
            public List<Message> loadMessages() {
                List<Message> messages = new LinkedList<Message>();
                messages.add(createDispMessage());
                messages.add(createAntMessage());
                messages.add(createDispExtMessage());
                return messages;
            }
        });
        converter.init();
    }

    @Test
    public void testUnsignedBytesToIntegerValues()
            throws UnknownIdentifierException {
        byte[] bytes0700 = { (byte) 0xF0, (byte) 0xF7, 0x1F, 0, (byte) 0xE2,
                0x44, (byte) 0xB1, 6 };
        Map<Signal, Double> integerValues = converter.bytesToIntegerValues(
                bytes0700, 0x700);
        assertEquals(Double.valueOf(127.0),
                integerValues.get(namesToSignals.get("geschwindigkeit")));
        assertEquals(Double.valueOf(511),
                integerValues.get(namesToSignals.get("gesamtkilometerstand")));
        assertEquals(Double.valueOf(2000),
                integerValues.get(namesToSignals.get("tageskilometerstand")));
        assertEquals(Double.valueOf(4),
                integerValues.get(namesToSignals.get("gang")));
        assertEquals(Double.valueOf(2),
                integerValues.get(namesToSignals.get("blinker")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("licht")));
        assertEquals(Double.valueOf(1),
                integerValues.get(namesToSignals.get("sitzgurt")));
        assertEquals(Double.valueOf(1),
                integerValues.get(namesToSignals.get("fahrertür")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("beifahrertür")));
        assertEquals(Double.valueOf(1),
                integerValues.get(namesToSignals.get("fronthaube")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("heckklappe")));
        assertEquals(Double.valueOf(1),
                integerValues.get(namesToSignals.get("fahrbereit")));
        assertEquals(Double.valueOf(1),
                integerValues.get(namesToSignals.get("ladekabel")));

        byte[] bytes701 = { 0x68, 0, 0x71, 0x14, 0x50, 0x55, 0x64, 0x2C };
        integerValues = converter.bytesToIntegerValues(bytes701, 0x701);
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("defrost")));
        assertEquals(Double.valueOf(42.5), integerValues.get(namesToSignals
                .get("geschwindigkeitSpeedlimiter")));
        assertEquals(Double.valueOf(20),
                integerValues.get(namesToSignals.get("innentemperatur")));
        assertEquals(Double.valueOf(-8),
                integerValues.get(namesToSignals.get("aussentemperatur")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("speedlimiter")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("sicherheitssysteme")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("abs")));
        assertEquals(Double.valueOf(0),
                integerValues.get(namesToSignals.get("esp")));
        assertEquals(Double.valueOf(3),
                integerValues.get(namesToSignals.get("kollisionsassistent")));
        assertEquals(Double.valueOf(1),
                integerValues.get(namesToSignals.get("airbag")));
        assertEquals(Double.valueOf(56.5),
                integerValues.get(namesToSignals.get("ladezustand")));
        assertEquals(Double.valueOf(20),
                integerValues.get(namesToSignals.get("energieverbrauch")));
        assertEquals(Double.valueOf(40),
                integerValues.get(namesToSignals.get("brennerfüllstand")));
    }

    @Test
    public void testSignedBytesToIntegerValues()
            throws UnknownIdentifierException {
        byte[] bytes711 = { (byte) 0xEC, (byte) 0xFF, (byte) 0xB8, 0xB, 0x19,
                0, (byte) 0xF0, (byte) 0xD8 };
        Map<Signal, Double> integerValues = converter.bytesToIntegerValues(
                bytes711, 0x711);
        assertEquals(Double.valueOf(30),
                integerValues.get(namesToSignals.get("torqueValue")));
        assertEquals(Double.valueOf(25),
                integerValues.get(namesToSignals.get("leistungInv")));
        assertEquals(Double.valueOf(-20),
                integerValues.get(namesToSignals.get("leistungTVI")));
        assertEquals(Double.valueOf(-100),
                integerValues.get(namesToSignals.get("INVTorqueValue")));
    }

    @Test
    public void testIntegerValuesToBytes() {

    }

    public Message createDispMessage() {
        Message disp = new Message();
        disp.setDataLengthCode(8);
        disp.setIdentifier(0x700);
        disp.setName("WPC_Disp");
        disp.setSenderType(SenderType.RECEIVER);
        List<Signal> disp_signals = new LinkedList<Signal>();
        Signal geschwindigkeit = new Signal(0, 0, 12, -128, 0.0625,
                "FZG_v_istx", true, disp);
        namesToSignals.put("geschwindigkeit", geschwindigkeit);
        Signal gesamtkilometerstand = new Signal(4, 1, 16, 0, 1, "FZG_s_geskm",
                false, disp);
        namesToSignals.put("gesamtkilometerstand", gesamtkilometerstand);
        Signal tageskilometerstand = new Signal(4, 3, 16, 0, 0.1,
                "FZG_s_tagkm", false, disp);
        namesToSignals.put("tageskilometerstand", tageskilometerstand);
        Signal gang_disp = new Signal(4, 5, 3, 0, 1, "FZG_ST_istGang", false,
                disp);
        namesToSignals.put("gang", gang_disp);
        Signal blinker_disp = new Signal(7, 5, 2, 0, 1, "FZG_ST_istBlinker",
                false, disp);
        namesToSignals.put("blinker", blinker_disp);
        Signal licht_disp = new Signal(1, 6, 3, 0, 1, "FZG_ST_istLicht", false,
                disp);
        namesToSignals.put("licht", licht_disp);
        Signal sitzgurt = new Signal(4, 6, 1, 0, 1, "FZG_STb_Sitzgurt", false,
                disp);
        namesToSignals.put("sitzgurt", sitzgurt);
        Signal tuerfahrer = new Signal(5, 6, 1, 0, 1, "FZG_STb_TuerFahrer",
                false, disp);
        namesToSignals.put("fahrertür", tuerfahrer);
        Signal tuerbeifahrer = new Signal(6, 6, 1, 0, 1,
                "FZG_STb_TuerBeifahrer", false, disp);
        namesToSignals.put("beifahrertür", tuerbeifahrer);
        Signal fronthaube = new Signal(7, 6, 1, 0, 1, "FZG_STb_TuerFront",
                false, disp);
        namesToSignals.put("fronthaube", fronthaube);
        Signal heckklappe = new Signal(0, 7, 1, 0, 1, "FZG_STb_TuerHeck",
                false, disp);
        namesToSignals.put("heckklappe", heckklappe);
        Signal fahrbereit = new Signal(1, 7, 1, 0, 1, "FZG_STb_Startbereit",
                false, disp);
        namesToSignals.put("fahrbereit", fahrbereit);
        Signal ladekabel = new Signal(2, 7, 1, 0, 1, "FZG_STb_Ladekabel",
                false, disp);
        namesToSignals.put("ladekabel", ladekabel);
        disp_signals.add(geschwindigkeit);
        disp_signals.add(gesamtkilometerstand);
        disp_signals.add(tageskilometerstand);
        disp_signals.add(gang_disp);
        disp_signals.add(blinker_disp);
        disp_signals.add(licht_disp);
        disp_signals.add(sitzgurt);
        disp_signals.add(tuerfahrer);
        disp_signals.add(tuerbeifahrer);
        disp_signals.add(fronthaube);
        disp_signals.add(heckklappe);
        disp_signals.add(fahrbereit);
        disp_signals.add(ladekabel);
        disp.setSignals(disp_signals);
        ParameterReference geschwindigkeit_param = new ParameterReference();
        geschwindigkeit_param.setName("geschwindigkeit");
        geschwindigkeit_param.setReadSignal(geschwindigkeit);
        geschwindigkeit.setParameterReference(geschwindigkeit_param);

        ParameterReference gesamtkmstand_param = new ParameterReference();
        gesamtkmstand_param.setName("gesamtkilometerstand");
        gesamtkmstand_param.setReadSignal(gesamtkilometerstand);
        gesamtkilometerstand.setParameterReference(gesamtkmstand_param);

        ParameterReference tageskmstand_param = new ParameterReference();
        tageskmstand_param.setName("tageskilometerstand");
        tageskmstand_param.setReadSignal(tageskilometerstand);
        tageskilometerstand.setParameterReference(tageskmstand_param);

        ParameterReference sitzgurt_param = new ParameterReference();
        sitzgurt_param.setName("sitzgurt");
        sitzgurt_param.setReadSignal(sitzgurt);
        sitzgurt.setParameterReference(sitzgurt_param);

        ParameterReference tuerfahrer_param = new ParameterReference();
        tuerfahrer_param.setName("fahrertuer");
        tuerfahrer_param.setReadSignal(tuerfahrer);
        tuerfahrer.setParameterReference(tuerfahrer_param);

        ParameterReference tuerbeifahrer_param = new ParameterReference();
        tuerbeifahrer_param.setName("beifahrertuer");
        tuerbeifahrer_param.setReadSignal(tuerbeifahrer);
        tuerbeifahrer.setParameterReference(tuerbeifahrer_param);

        ParameterReference fronthaube_param = new ParameterReference();
        fronthaube_param.setName("fronthaube");
        fronthaube_param.setReadSignal(fronthaube);
        fronthaube.setParameterReference(fronthaube_param);

        ParameterReference heckklappe_param = new ParameterReference();
        heckklappe_param.setName("heckklappe");
        heckklappe_param.setReadSignal(heckklappe);
        heckklappe.setParameterReference(heckklappe_param);

        ParameterReference fahrbereit_param = new ParameterReference();
        fahrbereit_param.setName("fahrbereit");
        fahrbereit_param.setReadSignal(fahrbereit);
        fahrbereit.setParameterReference(fahrbereit_param);

        ParameterReference ladekabel_param = new ParameterReference();
        ladekabel_param.setName("ladekabel");
        ladekabel_param.setReadSignal(ladekabel);
        ladekabel.setParameterReference(ladekabel_param);

        ParameterReference blinker = new ParameterReference();
        blinker.setName("blinker");
        blinker.setReadSignal(blinker_disp);
        blinker_disp.setParameterReference(blinker);

        ParameterReference licht = new ParameterReference();
        licht.setName("licht");
        licht.setReadSignal(licht_disp);
        licht_disp.setParameterReference(licht);

        ParameterReference gang = new ParameterReference();
        gang.setName("gang");
        gang.setReadSignal(gang_disp);
        gang_disp.setParameterReference(gang);
        return disp;
    }

    public Message createAntMessage() {
        Message deb_ant = new Message();
        deb_ant.setDataLengthCode(8);
        deb_ant.setIdentifier(0x711);
        List<Signal> debAntSignals = new LinkedList<Signal>();
        Signal tvi = new Signal(0, 0, 16, -32767, 1, "TVI_PwrAct", true,
                deb_ant);
        namesToSignals.put("leistungTVI", tvi);
        Signal torque_value = new Signal(0, 2, 16, -327.68, 0.01, "TVI_TrqAct",
                true, deb_ant);
        namesToSignals.put("torqueValue", torque_value);
        Signal inv = new Signal(0, 4, 16, -32767, 1, "DCAC_PwrAct", true,
                deb_ant);
        namesToSignals.put("leistungInv", inv);
        Signal inv_torque = new Signal(0, 6, 16, -327.68, 0.01, "DCAC_TrqAct",
                true, deb_ant);
        namesToSignals.put("INVTorqueValue", inv_torque);
        debAntSignals.add(tvi);
        debAntSignals.add(torque_value);
        debAntSignals.add(inv);
        debAntSignals.add(inv_torque);
        deb_ant.setSignals(debAntSignals);

        ParameterReference tvi_param = new ParameterReference();
        tvi_param.setName("leistungTVI");
        tvi_param.setReadSignal(tvi);
        tvi.setParameterReference(tvi_param);

        ParameterReference torque_value_param = new ParameterReference();
        torque_value_param.setName("torqueValue");
        torque_value_param.setReadSignal(torque_value);
        torque_value.setParameterReference(torque_value_param);

        ParameterReference inv_param = new ParameterReference();
        inv_param.setName("leistungInv");
        inv_param.setReadSignal(inv);
        inv.setParameterReference(inv_param);

        ParameterReference inv_torque_param = new ParameterReference();
        inv_torque_param.setName("INVTorqueValue");
        inv_torque_param.setReadSignal(inv_torque);
        inv_torque.setParameterReference(inv_torque_param);
        return deb_ant;
    }

    public Message createDispExtMessage() {
        Message disp_ext = new Message();
        disp_ext.setDataLengthCode(8);
        disp_ext.setIdentifier(0x701);
        disp_ext.setName("WPC_Disp_ext");
        disp_ext.setSenderType(SenderType.RECEIVER);
        List<Signal> disp_ext_signals = new LinkedList<Signal>();
        Signal stbAbs = new Signal(0, 0, 1, 0, 1, "FZG_STb_ABS", false,
                disp_ext);
        namesToSignals.put("abs", stbAbs);
        Signal stbEsp = new Signal(1, 0, 1, 0, 1, "FZG_STb_ESP", false,
                disp_ext);
        namesToSignals.put("esp", stbEsp);
        Signal stKollision = new Signal(5, 0, 3, 0, 1, "FZG_ST_Kollision",
                false, disp_ext);
        namesToSignals.put("kollisionsassistent", stKollision);
        Signal sicherheit = new Signal(2, 0, 1, 0, 1, "FZG_STb_Safety", false,
                disp_ext);
        namesToSignals.put("sicherheitssysteme", sicherheit);
        Signal airbag = new Signal(3, 0, 2, 0, 1, "FZG_ST_Airbag", false,
                disp_ext);
        namesToSignals.put("airbag", airbag);
        Signal speedlimiter = new Signal(0, 1, 1, 0, 1, "FZG_v_Speedlimiter",
                false, disp_ext);
        namesToSignals.put("speedlimiter", speedlimiter);
        Signal defrost = new Signal(1, 1, 1, 0, 1, "FZG_STb_Defrost", false,
                disp_ext);
        namesToSignals.put("defrost", defrost);
        Signal soc = new Signal(0, 2, 8, 0, 0.5, "FZG_rel_SOC", false, disp_ext);
        namesToSignals.put("ladezustand", soc);
        Signal energie = new Signal(0, 3, 8, -100, 1,
                "FZG_rel_Energieverbrauch", true, disp_ext);
        namesToSignals.put("energieverbrauch", energie);
        Signal brenner = new Signal(0, 4, 8, 0, 0.5, "FZG_rel_Brenner", false,
                disp_ext);
        namesToSignals.put("brennerfüllstand", brenner);
        Signal vSpeedlimiter = new Signal(0, 5, 8, 0, 0.5,
                "FZG_v_Speedlimiter", false, disp_ext);
        namesToSignals.put("geschwindigkeitSpeedlimiter", vSpeedlimiter);
        Signal innen = new Signal(0, 6, 8, -30, 0.5, "FZG_T_Innen", false,
                disp_ext);
        namesToSignals.put("innentemperatur", innen);
        Signal aussen = new Signal(0, 7, 8, -30, 0.5, "FZG_T_Aussen", false,
                disp_ext);
        namesToSignals.put("aussentemperatur", aussen);
        disp_ext_signals.add(stbEsp);
        disp_ext_signals.add(stKollision);
        disp_ext_signals.add(stbAbs);
        disp_ext_signals.add(sicherheit);
        disp_ext_signals.add(airbag);
        disp_ext_signals.add(speedlimiter);
        disp_ext_signals.add(defrost);
        disp_ext_signals.add(soc);
        disp_ext_signals.add(energie);
        disp_ext_signals.add(brenner);
        disp_ext_signals.add(vSpeedlimiter);
        disp_ext_signals.add(innen);
        disp_ext_signals.add(aussen);
        disp_ext.setSignals(disp_ext_signals);

        ParameterReference abs = new ParameterReference();
        abs.setName("abs");
        abs.setReadSignal(stbAbs);
        stbAbs.setParameterReference(abs);

        ParameterReference esp = new ParameterReference();
        esp.setName("esp");
        esp.setReadSignal(stbEsp);
        stbEsp.setParameterReference(esp);

        ParameterReference safety = new ParameterReference();
        safety.setName("sicherheitssysteme");
        safety.setReadSignal(sicherheit);
        sicherheit.setParameterReference(safety);

        ParameterReference airbag_param = new ParameterReference();
        airbag_param.setName("airbag");
        airbag_param.setReadSignal(airbag);
        airbag.setParameterReference(airbag_param);

        ParameterReference speedlimit_param = new ParameterReference();
        speedlimit_param.setName("speedlimiter");
        speedlimit_param.setReadSignal(speedlimiter);
        speedlimiter.setParameterReference(speedlimit_param);

        ParameterReference defrost_param = new ParameterReference();
        defrost_param.setName("defrost");
        defrost_param.setReadSignal(defrost);
        defrost.setParameterReference(defrost_param);

        ParameterReference soc_param = new ParameterReference();
        soc_param.setName("ladezustand");
        soc_param.setReadSignal(soc);
        soc.setParameterReference(soc_param);

        ParameterReference energie_param = new ParameterReference();
        energie_param.setName("energieverbrauch");
        energie_param.setReadSignal(energie);
        energie.setParameterReference(energie_param);

        ParameterReference brenner_param = new ParameterReference();
        brenner_param.setName("brennerfüllstand");
        brenner_param.setReadSignal(brenner);
        brenner.setParameterReference(brenner_param);

        ParameterReference vSpeed_param = new ParameterReference();
        vSpeed_param.setName("geschwindigkeitSpeedlimiter");
        vSpeed_param.setReadSignal(vSpeedlimiter);
        vSpeedlimiter.setParameterReference(vSpeed_param);

        ParameterReference innen_param = new ParameterReference();
        innen_param.setName("innentemperatur");
        innen_param.setReadSignal(innen);
        innen.setParameterReference(innen_param);

        ParameterReference aussen_param = new ParameterReference();
        aussen_param.setName("aussentemperatur");
        aussen_param.setReadSignal(aussen);
        aussen.setParameterReference(aussen_param);

        ParameterReference kollassist = new ParameterReference();
        kollassist.setName("kollisionsassistent");
        kollassist.setReadSignal(stKollision);
        stKollision.setParameterReference(kollassist);
        return disp_ext;
    }

}
