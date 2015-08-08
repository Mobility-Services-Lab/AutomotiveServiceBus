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
package de.visiom.carpc.can.impl.fixture;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.entities.Message.SenderType;
import de.visiom.carpc.can.entities.ParameterReference;
import de.visiom.carpc.can.entities.Signal;
import de.visiom.carpc.can.entities.StateMapping;

public class DatabaseFixture {

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseFixture.class);

	private static final String CAN_SERVICE_NAME = "can";

	public void fillDatabase(EntityManagerFactory entityManagerFactory) {
		Message disp = new Message();
		disp.setDataLengthCode(8);
		disp.setIdentifier(0x700);
		disp.setName("WPC_Disp");
		disp.setSenderType(SenderType.RECEIVER);
		List<Signal> disp_signals = new LinkedList<Signal>();
		Signal geschwindigkeit = new Signal(0, 0, 12, -128, 0.0625,
				"FZG_v_istx", true, disp);
		Signal gesamtkilometerstand = new Signal(4, 1, 16, 0, 1, "FZG_s_geskm",
				false, disp);
		Signal tageskilometerstand = new Signal(4, 3, 16, 0, 0.1,
				"FZG_s_tagkm", false, disp);
		Signal gang_disp = new Signal(4, 5, 3, 0, 1, "FZG_ST_istGang", false,
				disp);
		Signal blinker_disp = new Signal(7, 5, 2, 0, 1, "FZG_ST_istBlinker",
				false, disp);
		Signal licht_disp = new Signal(1, 6, 3, 0, 1, "FZG_ST_istLicht", false,
				disp);
		Signal sitzgurt = new Signal(4, 6, 1, 0, 1, "FZG_STb_Sitzgurt", false,
				disp);
		Signal tuerfahrer = new Signal(5, 6, 1, 0, 1, "FZG_STb_TuerFahrer",
				false, disp);
		Signal tuerbeifahrer = new Signal(6, 6, 1, 0, 1,
				"FZG_STb_TuerBeifahrer", false, disp);
		Signal fronthaube = new Signal(7, 6, 1, 0, 1, "FZG_STb_TuerFront",
				false, disp);
		Signal heckklappe = new Signal(0, 7, 1, 0, 1, "FZG_STb_TuerHeck",
				false, disp);
		Signal fahrbereit = new Signal(1, 7, 1, 0, 1, "FZG_STb_Startbereit",
				false, disp);
		Signal ladekabel = new Signal(2, 7, 1, 0, 1, "FZG_STb_Ladekabel",
				false, disp);
		Signal nebelschluss = new Signal(3, 7, 1, 0, 1, "LIB_STb_istNebLi",
				false, disp);
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
		disp_signals.add(nebelschluss);
		disp.setSignals(disp_signals);

		Message disp_ext = new Message();
		disp_ext.setDataLengthCode(8);
		disp_ext.setIdentifier(0x701);
		disp_ext.setName("WPC_Disp_ext");
		disp_ext.setSenderType(SenderType.RECEIVER);
		List<Signal> disp_ext_signals = new LinkedList<Signal>();
		Signal stbAbs = new Signal(0, 0, 1, 0, 1, "FZG_STb_ABS", false,
				disp_ext);
		Signal stbEsp = new Signal(1, 0, 1, 0, 1, "FZG_STb_ESP", false,
				disp_ext);
		Signal stKollision = new Signal(5, 0, 3, 0, 1, "FZG_ST_Kollision",
				false, disp_ext);
		Signal sicherheit = new Signal(2, 0, 1, 0, 1, "FZG_STb_Safety", false,
				disp_ext);
		Signal airbag = new Signal(3, 0, 2, 0, 1, "FZG_ST_Airbag", false,
				disp_ext);
		Signal speedlimiter = new Signal(0, 1, 1, 0, 1, "FZG_v_Speedlimiter",
				false, disp_ext);
		Signal defrost = new Signal(1, 1, 1, 0, 1, "FZG_STb_Defrost", false,
				disp_ext);
		Signal soc = new Signal(0, 2, 8, 0, 0.5, "FZG_rel_SOC", false, disp_ext);
		Signal energie = new Signal(0, 3, 8, -30, 0.5,
				"FZG_rel_Energieverbrauch", false, disp_ext);
		Signal brenner = new Signal(0, 4, 8, 0, 0.5, "FZG_rel_Brenner", false,
				disp_ext);
		Signal vSpeedlimiter = new Signal(0, 5, 8, 0, 0.5,
				"FZG_v_Speedlimiter", false, disp_ext);
		Signal innen = new Signal(0, 6, 8, -30, 0.5, "FZG_T_Innen", false,
				disp_ext);
		Signal aussen = new Signal(0, 7, 8, -30, 0.5, "FZG_T_Aussen", false,
				disp_ext);
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

		Message disp_ext_2 = new Message();
		disp_ext_2.setDataLengthCode(8);
		disp_ext_2.setIdentifier(0x703);
		disp_ext_2.setName("WPC_Disp_ext_2");
		disp_ext_2.setSenderType(SenderType.RECEIVER);
		List<Signal> disp_ext_2_signals = new LinkedList<Signal>();
		Signal laut_down = new Signal(0, 0, 1, 0, 1, "WPC_Laut_down", false,
				disp_ext_2);
		Signal laut_press = new Signal(1, 0, 1, 0, 1, "WPC_Laut_press", false,
				disp_ext_2);
		Signal laut_up = new Signal(2, 0, 1, 0, 1, "WPC_Laut_up", false,
				disp_ext_2);
		Signal ptt = new Signal(3, 0, 1, 0, 1, "WPC_PtT", false, disp_ext_2);
		Signal pb_aktiv = new Signal(4, 0, 1, 0, 1, "WPC_PB_aktiv", false,
				disp_ext_2);
		Signal shutdown = new Signal(7, 0, 1, 0, 1, "WPC_shutdown", false,
				disp_ext_2);
		disp_ext_2_signals.add(laut_down);
		disp_ext_2_signals.add(laut_press);
		disp_ext_2_signals.add(laut_up);
		disp_ext_2_signals.add(ptt);
		disp_ext_2_signals.add(pb_aktiv);
		disp_ext_2_signals.add(shutdown);
		disp_ext_2.setSignals(disp_ext_2_signals);

		Message clima_driver = new Message();
		clima_driver.setDataLengthCode(8);
		clima_driver.setIdentifier(0x702);
		clima_driver.setName("WPC_Clima_Driver");
		clima_driver.setSenderType(SenderType.TRANSCEIVER);
		List<Signal> climaDriverSignals = new LinkedList<Signal>();
		Signal relLuefter = new Signal(0, 0, 8, 0, .5, "FAH_rel_Luefter",
				false, clima_driver);
		Signal relKlappen = new Signal(0, 1, 8, 0, .5, "FAH_rel_Klappen",
				false, clima_driver);
		Signal relTemp = new Signal(0, 2, 8, -30, .5, "FAH_rel_Temp", false,
				clima_driver);
		Signal stKlima = new Signal(0, 3, 3, 0, 1, "FAH_ST_Klima", false,
				clima_driver);
		Signal stSitz = new Signal(3, 3, 3, 0, 1, "FAH_ST_Sitz", false,
				clima_driver);
		Signal stSitz2 = new Signal(5, 4, 3, 0, 1, "FAH_ST_Sitz_2", false,
				clima_driver);
		Signal stbUmluft = new Signal(6, 3, 1, 0, 1, "FAH_STb_Umluft", false,
				clima_driver);
		Signal stbDeaktAbs = new Signal(0, 4, 1, 0, 1, "FAH_STb_DeaktABS",
				false, clima_driver);
		Signal stbDeaktEsp = new Signal(1, 4, 1, 0, 1, "FAH_STb_DeaktESP",
				false, clima_driver);
		Signal stbDeaktTV = new Signal(2, 4, 1, 0, 1, "FAH_STb_DeaktTV", false,
				clima_driver);
		Signal stModus = new Signal(3, 4, 2, 0, 1, "FAH_ST_Modus", false,
				clima_driver);
		climaDriverSignals.add(relKlappen);
		climaDriverSignals.add(relLuefter);
		climaDriverSignals.add(relTemp);
		climaDriverSignals.add(stKlima);
		climaDriverSignals.add(stbUmluft);
		climaDriverSignals.add(stSitz);
		climaDriverSignals.add(stSitz2);
		climaDriverSignals.add(stbDeaktTV);
		climaDriverSignals.add(stbDeaktEsp);
		climaDriverSignals.add(stbDeaktAbs);
		climaDriverSignals.add(stModus);
		clima_driver.setSignals(climaDriverSignals);

		Message deb_sens = new Message();
		deb_sens.setDataLengthCode(8);
		deb_sens.setIdentifier(0x710);
		deb_sens.setName("WPC_DEB_Sens");
		deb_sens.setSenderType(SenderType.RECEIVER);
		List<Signal> debSensSignals = new LinkedList<Signal>();
		Signal sensorAY = new Signal(0, 0, 16, -3214.5408, 0.0981,
				"ESP_Vehicle_Lateral_Accel", true, deb_sens);
		Signal sensorAX = new Signal(0, 2, 16, 0, 0.0981,
				"ESP_Vehicle_Long_Accel", true, deb_sens);
		Signal sensorPSIP = new Signal(0, 4, 16, 0, 0.00286, "ESP_Yaw_Rate",
				true, deb_sens);
		debSensSignals.add(sensorAY);
		debSensSignals.add(sensorAX);
		debSensSignals.add(sensorPSIP);
		deb_sens.setSignals(debSensSignals);

		Message deb_ant = new Message();
		deb_ant.setDataLengthCode(8);
		deb_ant.setIdentifier(0x711);
		deb_ant.setName("WPC_DEB_ANT");
		deb_ant.setSenderType(SenderType.RECEIVER);
		List<Signal> debAntSignals = new LinkedList<Signal>();
		Signal tvi = new Signal(0, 0, 16, -32767, 1, "TVI_PwrAct", true,
				deb_ant);
		Signal torque_value = new Signal(0, 2, 16, -327.68, 0.01, "TVI_TrqAct",
				true, deb_ant);
		Signal inv = new Signal(0, 4, 16, -32767, 1, "DCAC_PwrAct", true,
				deb_ant);
		Signal inv_torque = new Signal(0, 6, 16, -327.68, 0.01, "DCAC_TrqAct",
				true, deb_ant);
		debAntSignals.add(tvi);
		debAntSignals.add(torque_value);
		debAntSignals.add(inv);
		debAntSignals.add(inv_torque);
		deb_ant.setSignals(debAntSignals);

		Message deb_brake = new Message();
		deb_brake.setDataLengthCode(8);
		deb_brake.setIdentifier(0x712);
		deb_brake.setName("WPC_DEB_Brake");
		deb_brake.setSenderType(SenderType.RECEIVER);
		List<Signal> debBrakeSignals = new LinkedList<Signal>();
		Signal bremsdruckVL = new Signal(0, 0, 16, 0, 0.01,
				"Brake_Fluid_Pressure_FL", false, deb_brake);
		Signal bremsdruckVR = new Signal(0, 2, 16, 0, 0.01,
				"Brake_Fluid_Pressure_FR", false, deb_brake);
		Signal bremsdruckHL = new Signal(0, 4, 16, 0, 0.01,
				"Brake_Fluid_Pressure_RL", false, deb_brake);
		Signal bremsdruckHR = new Signal(0, 6, 16, 0, 0.01,
				"Brake_Fluid_Pressure_RR", false, deb_brake);
		debBrakeSignals.add(bremsdruckVL);
		debBrakeSignals.add(bremsdruckVR);
		debBrakeSignals.add(bremsdruckHL);
		debBrakeSignals.add(bremsdruckHR);
		deb_brake.setSignals(debBrakeSignals);

		Message deb_wheels = new Message();
		deb_wheels.setDataLengthCode(8);
		deb_wheels.setIdentifier(0x713);
		deb_wheels.setName("WPC_DEB_Wheels");
		deb_wheels.setSenderType(SenderType.RECEIVER);
		List<Signal> debWheelsSignals = new LinkedList<Signal>();
		Signal radgeschwVL = new Signal(0, 0, 16, 0, 0.01, "Wheel_Speed_FL",
				false, deb_wheels);
		Signal radgeschwVR = new Signal(2, 0, 16, 0, 0.01, "Wheel_Speed_FR",
				false, deb_wheels);
		Signal radgeschwHL = new Signal(4, 0, 16, 0, 0.01, "Wheel_Speed_RL",
				false, deb_wheels);
		Signal radgeschwHR = new Signal(6, 0, 16, 0, 0.01, "Wheel_Speed_RR",
				false, deb_wheels);
		debWheelsSignals.add(radgeschwVL);
		debWheelsSignals.add(radgeschwVR);
		debWheelsSignals.add(radgeschwHL);
		debWheelsSignals.add(radgeschwHR);
		deb_wheels.setSignals(debWheelsSignals);

		Message deb_driv = new Message();
		deb_driv.setDataLengthCode(8);
		deb_driv.setIdentifier(0x714);
		deb_driv.setName("WPC_DEB_Driv");
		deb_driv.setSenderType(SenderType.RECEIVER);
		List<Signal> debDrivSignals = new LinkedList<Signal>();
		Signal lenkwinkel = new Signal(0, 0, 16, -1000, 0.1,
				"Steeringwheel_angle", true, deb_driv);
		Signal lenkwinkelgeschw = new Signal(0, 2, 8, 0, 4,
				"Steeringwheel_rate", false, deb_driv);
		Signal fahrpedalst = new Signal(0, 3, 8, 0, 0.4, "FAH_rel_istFP",
				false, deb_driv);
		Signal bremspedalst = new Signal(0, 4, 8, 0, 0.4, "FAH_rel_istBP",
				false, deb_driv);
		debDrivSignals.add(lenkwinkel);
		debDrivSignals.add(lenkwinkelgeschw);
		debDrivSignals.add(fahrpedalst);
		debDrivSignals.add(bremspedalst);
		deb_driv.setSignals(debDrivSignals);

		Message deb_hv = new Message();
		deb_hv.setDataLengthCode(8);
		deb_hv.setIdentifier(0x715);
		deb_hv.setName("WPC_DEB_HV");
		deb_hv.setSenderType(SenderType.RECEIVER);
		List<Signal> debHVSignals = new LinkedList<Signal>();
		Signal leistungHV = new Signal(0, 0, 16, -32767, 1, "FZG_P_HV", true,
				deb_hv);
		Signal hochvoltspannung = new Signal(0, 2, 10, 0, 0.5, "FZG_U_HV",
				false, deb_hv);
		Signal stromHV = new Signal(2, 3, 11, -102.3, 0.1, "FZG_I_HV", true,
				deb_hv);
		Signal hauptschuetz = new Signal(5, 4, 1, 0, 1, "BMS_STb_Hauptschuetz",
				false, deb_hv);
		Signal nvSchuetz = new Signal(6, 4, 1, 0, 1, "BMS_STb_NVSchuetz",
				false, deb_hv);
		Signal ladeschuetz = new Signal(7, 4, 1, 0, 1, "BMS_STb_Ladeschuetz",
				false, deb_hv);
		debHVSignals.add(leistungHV);
		debHVSignals.add(hochvoltspannung);
		debHVSignals.add(stromHV);
		debHVSignals.add(hauptschuetz);
		debHVSignals.add(nvSchuetz);
		debHVSignals.add(ladeschuetz);
		deb_hv.setSignals(debHVSignals);

		Message deb_nv = new Message();
		deb_nv.setDataLengthCode(8);
		deb_nv.setIdentifier(0x716);
		deb_nv.setName("WPC_DEB_NV");
		deb_nv.setSenderType(SenderType.RECEIVER);
		List<Signal> debNVSignals = new LinkedList<Signal>();
		Signal leistungNV = new Signal(0, 0, 12, 0, 1, "FZG_P_NV", false,
				deb_nv);
		Signal niedervoltspannung = new Signal(4, 1, 5, 0, 0.5, "FZG_U_NV",
				false, deb_nv);
		Signal stromNV = new Signal(1, 2, 12, -204.7, 0.1, "FZG_I_NV", true,
				deb_nv);
		Signal kl30l = new Signal(4, 3, 1, 0, 1, "FZG_STb_KL30L", false, deb_nv);
		Signal kl30b = new Signal(5, 3, 1, 0, 1, "FZG_STb_KL30B", false, deb_nv);
		Signal kl15 = new Signal(6, 3, 1, 0, 1, "FZG_STb_KL15", false, deb_nv);
		Signal ucu = new Signal(0, 4, 3, 0, 1, "FZG_ST_istSystem", false,
				deb_nv);
		debNVSignals.add(leistungNV);
		debNVSignals.add(niedervoltspannung);
		debNVSignals.add(stromNV);
		debNVSignals.add(kl30l);
		debNVSignals.add(kl30b);
		debNVSignals.add(kl15);
		debNVSignals.add(ucu);
		deb_nv.setSignals(debNVSignals);

		Message deb_temp = new Message();
		deb_temp.setDataLengthCode(8);
		deb_temp.setIdentifier(0x717);
		deb_temp.setName("WPC_DEB_TEMP");
		deb_temp.setSenderType(SenderType.RECEIVER);
		List<Signal> debTempSignals = new LinkedList<Signal>();
		Signal tempAkku = new Signal(0, 0, 8, -30, 0.5, "FZG_T_HV", false,
				deb_temp);
		Signal tempGetriebe = new Signal(0, 1, 8, -30, 0.5, "FZG_T_Getr",
				false, deb_temp);
		Signal tempKuehl = new Signal(0, 2, 8, -30, 0.5, "FZG_T_Kuehl", false,
				deb_temp);
		Signal tempKuehl2 = new Signal(0, 3, 8, -30, 0.5, "FZG_T_Kuehl2",
				false, deb_temp);
		Signal tempPeltier = new Signal(0, 4, 8, -30, 0.5, "FZG_T_Peltiers",
				false, deb_temp);
		debTempSignals.add(tempAkku);
		debTempSignals.add(tempGetriebe);
		debTempSignals.add(tempKuehl);
		debTempSignals.add(tempKuehl2);
		debTempSignals.add(tempPeltier);
		deb_temp.setSignals(debTempSignals);

		Message debSettingsDriver = new Message();
		debSettingsDriver.setDataLengthCode(4);
		debSettingsDriver.setIdentifier(0x718);
		debSettingsDriver.setName("WPC_DEB_SettingsDriver");
		debSettingsDriver.setSenderType(SenderType.TRANSCEIVER);
		List<Signal> debSettingsDriverSignals = new LinkedList<Signal>();
		Signal sollBlinker = new Signal(0, 0, 2, 0, 1, "DEB_ST_sollBlinker",
				false, debSettingsDriver);
		Signal sollLicht = new Signal(2, 0, 3, 0, 1, "DEB_ST_sollLicht", false,
				debSettingsDriver);
		Signal sollGang = new Signal(5, 0, 3, 0, 1, "DEB_ST_sollGang", false,
				debSettingsDriver);
		Signal sollHupe = new Signal(0, 1, 1, 0, 1, "DEB_ST_sollHupe", false,
				debSettingsDriver);
		debSettingsDriverSignals.add(sollBlinker);
		debSettingsDriverSignals.add(sollLicht);
		debSettingsDriverSignals.add(sollGang);
		debSettingsDriverSignals.add(sollHupe);
		debSettingsDriver.setSignals(debSettingsDriverSignals);

		ParameterReference geschwindigkeit_param = new ParameterReference();
		geschwindigkeit_param.setName("geschwindigkeit");
		geschwindigkeit_param.setServiceName(CAN_SERVICE_NAME);
		geschwindigkeit_param.setReadSignal(geschwindigkeit);
		geschwindigkeit.setParameterReference(geschwindigkeit_param);

		ParameterReference gesamtkmstand_param = new ParameterReference();
		gesamtkmstand_param.setName("gesamtkilometerstand");
		gesamtkmstand_param.setServiceName(CAN_SERVICE_NAME);
		gesamtkmstand_param.setReadSignal(gesamtkilometerstand);
		gesamtkilometerstand.setParameterReference(gesamtkmstand_param);

		ParameterReference tageskmstand_param = new ParameterReference();
		tageskmstand_param.setName("tageskilometerstand");
		tageskmstand_param.setServiceName(CAN_SERVICE_NAME);
		tageskmstand_param.setReadSignal(tageskilometerstand);
		tageskilometerstand.setParameterReference(tageskmstand_param);

		ParameterReference sitzgurt_param = new ParameterReference();
		sitzgurt_param.setName("sitzgurt");
		sitzgurt_param.setServiceName(CAN_SERVICE_NAME);
		sitzgurt_param.setReadSignal(sitzgurt);
		sitzgurt.setParameterReference(sitzgurt_param);

		ParameterReference tuerfahrer_param = new ParameterReference();
		tuerfahrer_param.setName("fahrertür");
		tuerfahrer_param.setServiceName(CAN_SERVICE_NAME);
		tuerfahrer_param.setReadSignal(tuerfahrer);
		List<StateMapping> tuerfahrer_mappings = new LinkedList<StateMapping>();
		tuerfahrer_mappings.add(new StateMapping("Geöffnet", 0D));
		tuerfahrer_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping sm : tuerfahrer_mappings) {
			sm.setParameter(tuerfahrer_param);
		}
		tuerfahrer_param.setStateMappings(tuerfahrer_mappings);
		tuerfahrer.setParameterReference(tuerfahrer_param);

		ParameterReference tuerbeifahrer_param = new ParameterReference();
		tuerbeifahrer_param.setName("beifahrertür");
		tuerbeifahrer_param.setServiceName(CAN_SERVICE_NAME);
		tuerbeifahrer_param.setReadSignal(tuerbeifahrer);
		List<StateMapping> tuerbeifahrer_mappings = new LinkedList<StateMapping>();
		tuerbeifahrer_mappings.add(new StateMapping("Geöffnet", 0D));
		tuerbeifahrer_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping sm : tuerbeifahrer_mappings) {
			sm.setParameter(tuerbeifahrer_param);
		}
		tuerbeifahrer_param.setStateMappings(tuerbeifahrer_mappings);
		tuerbeifahrer.setParameterReference(tuerbeifahrer_param);

		ParameterReference fronthaube_param = new ParameterReference();
		fronthaube_param.setName("fronthaube");
		fronthaube_param.setServiceName(CAN_SERVICE_NAME);
		fronthaube_param.setReadSignal(fronthaube);
		List<StateMapping> fronthaube_mappings = new LinkedList<StateMapping>();
		fronthaube_mappings.add(new StateMapping("Geöffnet", 0D));
		fronthaube_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping sm : fronthaube_mappings) {
			sm.setParameter(fronthaube_param);
		}
		fronthaube_param.setStateMappings(fronthaube_mappings);
		fronthaube.setParameterReference(fronthaube_param);

		ParameterReference heckklappe_param = new ParameterReference();
		heckklappe_param.setName("heckklappe");
		heckklappe_param.setServiceName(CAN_SERVICE_NAME);
		heckklappe_param.setReadSignal(heckklappe);
		List<StateMapping> heckklappe_mappings = new LinkedList<StateMapping>();
		heckklappe_mappings.add(new StateMapping("Geöffnet", 0D));
		heckklappe_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping sm : heckklappe_mappings) {
			sm.setParameter(heckklappe_param);
		}
		heckklappe_param.setStateMappings(heckklappe_mappings);
		heckklappe.setParameterReference(heckklappe_param);

		ParameterReference fahrbereit_param = new ParameterReference();
		fahrbereit_param.setName("fahrbereit");
		fahrbereit_param.setServiceName(CAN_SERVICE_NAME);
		fahrbereit_param.setReadSignal(fahrbereit);
		List<StateMapping> fahrbereit_mappings = new LinkedList<StateMapping>();
		fahrbereit_mappings.add(new StateMapping("Nicht Fahrbereit", 0D));
		fahrbereit_mappings.add(new StateMapping("Fahrbereit", 1D));
		for (StateMapping sm : fahrbereit_mappings) {
			sm.setParameter(fahrbereit_param);
		}
		fahrbereit_param.setStateMappings(fahrbereit_mappings);
		fahrbereit.setParameterReference(fahrbereit_param);

		ParameterReference ladekabel_param = new ParameterReference();
		ladekabel_param.setName("ladekabel");
		ladekabel_param.setServiceName(CAN_SERVICE_NAME);
		ladekabel_param.setReadSignal(ladekabel);
		ladekabel.setParameterReference(ladekabel_param);

		ParameterReference nebelschluss_param = new ParameterReference();
		nebelschluss_param.setName("nebelschlussleuchte");
		nebelschluss_param.setServiceName(CAN_SERVICE_NAME);
		nebelschluss_param.setReadSignal(nebelschluss);
		nebelschluss.setParameterReference(nebelschluss_param);

		ParameterReference abs = new ParameterReference();
		abs.setName("abs");
		abs.setServiceName(CAN_SERVICE_NAME);
		abs.setReadSignal(stbAbs);
		stbAbs.setParameterReference(abs);

		ParameterReference absdeaktivieren = new ParameterReference();
		absdeaktivieren.setName("absdeaktivieren");
		absdeaktivieren.setServiceName(CAN_SERVICE_NAME);
		absdeaktivieren.setWriteSignal(stbDeaktAbs);
		stbDeaktAbs.setParameterReference(absdeaktivieren);

		ParameterReference esp = new ParameterReference();
		esp.setName("esp");
		esp.setServiceName(CAN_SERVICE_NAME);
		esp.setReadSignal(stbEsp);
		stbEsp.setParameterReference(esp);

		ParameterReference espdeaktivieren = new ParameterReference();
		espdeaktivieren.setName("espdeaktivieren");
		espdeaktivieren.setServiceName(CAN_SERVICE_NAME);
		espdeaktivieren.setWriteSignal(stbDeaktEsp);
		stbDeaktEsp.setParameterReference(espdeaktivieren);

		ParameterReference safety = new ParameterReference();
		safety.setName("sicherheitssysteme");
		safety.setServiceName(CAN_SERVICE_NAME);
		safety.setReadSignal(sicherheit);
		sicherheit.setParameterReference(safety);

		ParameterReference airbag_param = new ParameterReference();
		airbag_param.setName("airbag");
		airbag_param.setServiceName(CAN_SERVICE_NAME);
		airbag_param.setReadSignal(airbag);
		airbag.setParameterReference(airbag_param);
		List<StateMapping> airbagMappings = new LinkedList<StateMapping>();
		airbagMappings.add(new StateMapping("Kein Fehler", 0D));
		airbagMappings.add(new StateMapping("Initialisierung", 1D));
		airbagMappings.add(new StateMapping("Nicht verfügbar", 2D));
		for (StateMapping sm : airbagMappings) {
			sm.setParameter(airbag_param);
		}
		airbag_param.setStateMappings(airbagMappings);

		ParameterReference speedlimit_param = new ParameterReference();
		speedlimit_param.setName("speedlimiter");
		speedlimit_param.setServiceName(CAN_SERVICE_NAME);
		speedlimit_param.setReadSignal(speedlimiter);
		speedlimiter.setParameterReference(speedlimit_param);

		ParameterReference defrost_param = new ParameterReference();
		defrost_param.setName("defrost");
		defrost_param.setServiceName(CAN_SERVICE_NAME);
		defrost_param.setReadSignal(defrost);
		defrost.setParameterReference(defrost_param);

		ParameterReference soc_param = new ParameterReference();
		soc_param.setName("ladezustand");
		soc_param.setServiceName(CAN_SERVICE_NAME);
		soc_param.setReadSignal(soc);
		soc.setParameterReference(soc_param);

		ParameterReference energie_param = new ParameterReference();
		energie_param.setName("energieverbrauch");
		energie_param.setServiceName(CAN_SERVICE_NAME);
		energie_param.setReadSignal(energie);
		energie.setParameterReference(energie_param);

		ParameterReference brenner_param = new ParameterReference();
		brenner_param.setName("brennerfüllstand");
		brenner_param.setServiceName(CAN_SERVICE_NAME);
		brenner_param.setReadSignal(brenner);
		brenner.setParameterReference(brenner_param);

		ParameterReference vSpeed_param = new ParameterReference();
		vSpeed_param.setName("geschwindigkeitSpeedlimiter");
		vSpeed_param.setServiceName(CAN_SERVICE_NAME);
		vSpeed_param.setReadSignal(vSpeedlimiter);
		vSpeedlimiter.setParameterReference(vSpeed_param);

		ParameterReference innen_param = new ParameterReference();
		innen_param.setName("innentemperatur");
		innen_param.setServiceName(CAN_SERVICE_NAME);
		innen_param.setReadSignal(innen);
		innen.setParameterReference(innen_param);

		ParameterReference aussen_param = new ParameterReference();
		aussen_param.setName("aussentemperatur");
		aussen_param.setServiceName(CAN_SERVICE_NAME);
		aussen_param.setReadSignal(aussen);
		aussen.setParameterReference(aussen_param);

		ParameterReference kollassist = new ParameterReference();
		kollassist.setServiceName(CAN_SERVICE_NAME);
		kollassist.setName("kollisionsassistent");
		kollassist.setReadSignal(stKollision);
		stKollision.setParameterReference(kollassist);
		List<StateMapping> kollAssistMappings = new LinkedList<StateMapping>();
		kollAssistMappings.add(new StateMapping("Keine Warnung", 0D));
		kollAssistMappings.add(new StateMapping("Front 1", 1D));
		kollAssistMappings.add(new StateMapping("Front 2", 2D));
		kollAssistMappings.add(new StateMapping("Seite 1", 3D));
		kollAssistMappings.add(new StateMapping("Seite 2", 4D));
		for (StateMapping sm : kollAssistMappings) {
			sm.setParameter(kollassist);
		}
		kollassist.setStateMappings(kollAssistMappings);

		ParameterReference volumeUp = new ParameterReference();
		volumeUp.setServiceName(CAN_SERVICE_NAME);
		volumeUp.setName("volumeUpPressed");
		volumeUp.setReadSignal(laut_up);
		laut_up.setParameterReference(volumeUp);

		ParameterReference volumeDown = new ParameterReference();
		volumeDown.setServiceName(CAN_SERVICE_NAME);
		volumeDown.setName("volumeDownPressed");
		volumeDown.setReadSignal(laut_down);
		laut_down.setParameterReference(volumeDown);

		ParameterReference volumePressed = new ParameterReference();
		volumePressed.setServiceName(CAN_SERVICE_NAME);
		volumePressed.setName("volumePressed");
		volumePressed.setReadSignal(laut_press);
		laut_press.setParameterReference(volumePressed);

		ParameterReference shutdownRef = new ParameterReference();
		shutdownRef.setServiceName(CAN_SERVICE_NAME);
		shutdownRef.setName("shutdown");
		shutdownRef.setReadSignal(shutdown);
		shutdown.setParameterReference(shutdownRef);

		ParameterReference luefterInnen = new ParameterReference();
		luefterInnen.setServiceName(CAN_SERVICE_NAME);
		luefterInnen.setName("lüfterInnenraum");
		luefterInnen.setWriteSignal(relLuefter);
		relLuefter.setParameterReference(luefterInnen);

		ParameterReference klappe = new ParameterReference();
		klappe.setServiceName(CAN_SERVICE_NAME);
		klappe.setName("klappenstellung");
		klappe.setWriteSignal(relKlappen);
		relKlappen.setParameterReference(klappe);

		ParameterReference tempvorgabe = new ParameterReference();
		tempvorgabe.setServiceName(CAN_SERVICE_NAME);
		tempvorgabe.setName("temperaturvorgabe");
		tempvorgabe.setWriteSignal(relTemp);
		relTemp.setParameterReference(tempvorgabe);

		ParameterReference klima = new ParameterReference();
		klima.setServiceName(CAN_SERVICE_NAME);
		klima.setName("klimatisierung");
		klima.setWriteSignal(stKlima);
		stKlima.setParameterReference(klima);
		List<StateMapping> klimaMappings = new LinkedList<StateMapping>();
		klimaMappings.add(new StateMapping("Aus", 0D));
		klimaMappings.add(new StateMapping("Automatik", 1D));
		klimaMappings.add(new StateMapping("Eco", 2D));
		klimaMappings.add(new StateMapping("0-Emission", 3D));
		klimaMappings.add(new StateMapping("Manuell", 4D));
		for (StateMapping sm : klimaMappings) {
			sm.setParameter(klima);
		}
		klima.setStateMappings(klimaMappings);

		ParameterReference sitzKlima = new ParameterReference();
		sitzKlima.setServiceName(CAN_SERVICE_NAME);
		sitzKlima.setName("sitzklimafahrer");
		sitzKlima.setWriteSignal(stSitz);
		stSitz.setParameterReference(sitzKlima);

		ParameterReference sitzKlima2 = new ParameterReference();
		sitzKlima2.setServiceName(CAN_SERVICE_NAME);
		sitzKlima2.setName("sitzklimabeifahrer");
		sitzKlima2.setWriteSignal(stSitz2);
		stSitz2.setParameterReference(sitzKlima2);

		ParameterReference umluft = new ParameterReference();
		umluft.setServiceName(CAN_SERVICE_NAME);
		umluft.setName("umluft");
		umluft.setWriteSignal(stbUmluft);
		stbUmluft.setParameterReference(umluft);

		ParameterReference tv = new ParameterReference();
		tv.setServiceName(CAN_SERVICE_NAME);
		tv.setName("tvdeaktivieren");
		tv.setWriteSignal(stbDeaktTV);
		stbDeaktTV.setParameterReference(tv);

		ParameterReference fahrmodus = new ParameterReference();
		fahrmodus.setServiceName(CAN_SERVICE_NAME);
		fahrmodus.setName("fahrmodus");
		fahrmodus.setWriteSignal(stModus);
		stModus.setParameterReference(fahrmodus);
		List<StateMapping> modusMapings = new LinkedList<StateMapping>();
		modusMapings.add(new StateMapping("Default", 0D));
		modusMapings.add(new StateMapping("Eco", 1D));
		modusMapings.add(new StateMapping("Comfort", 2D));
		modusMapings.add(new StateMapping("Sport", 3D));
		for (StateMapping sm : modusMapings) {
			sm.setParameter(fahrmodus);
		}
		fahrmodus.setStateMappings(modusMapings);

		ParameterReference latAcc_param = new ParameterReference();
		latAcc_param.setServiceName(CAN_SERVICE_NAME);
		latAcc_param.setName("lateralAcceleration");
		latAcc_param.setReadSignal(sensorAY);
		sensorAY.setParameterReference(latAcc_param);

		ParameterReference longAcc_param = new ParameterReference();
		longAcc_param.setServiceName(CAN_SERVICE_NAME);
		longAcc_param.setName("longAcceleration");
		longAcc_param.setReadSignal(sensorAX);
		sensorAX.setParameterReference(longAcc_param);

		ParameterReference yawRate_param = new ParameterReference();
		yawRate_param.setServiceName(CAN_SERVICE_NAME);
		yawRate_param.setName("yawRate");
		yawRate_param.setReadSignal(sensorPSIP);
		sensorPSIP.setParameterReference(yawRate_param);

		ParameterReference tvi_param = new ParameterReference();
		tvi_param.setServiceName(CAN_SERVICE_NAME);
		tvi_param.setName("leistungTVI");
		tvi_param.setReadSignal(tvi);
		tvi.setParameterReference(tvi_param);

		ParameterReference torque_value_param = new ParameterReference();
		torque_value_param.setServiceName(CAN_SERVICE_NAME);
		torque_value_param.setName("torqueValue");
		torque_value_param.setReadSignal(torque_value);
		torque_value.setParameterReference(torque_value_param);

		ParameterReference inv_param = new ParameterReference();
		inv_param.setServiceName(CAN_SERVICE_NAME);
		inv_param.setName("leistungInv");
		inv_param.setReadSignal(inv);
		inv.setParameterReference(inv_param);

		ParameterReference inv_torque_param = new ParameterReference();
		inv_torque_param.setServiceName(CAN_SERVICE_NAME);
		inv_torque_param.setName("INVTorqueValue");
		inv_torque_param.setReadSignal(inv_torque);
		inv_torque.setParameterReference(inv_torque_param);

		ParameterReference bremsVL_param = new ParameterReference();
		bremsVL_param.setServiceName(CAN_SERVICE_NAME);
		bremsVL_param.setName("bremsdruckVorneLinks");
		bremsVL_param.setReadSignal(bremsdruckVL);
		bremsdruckVL.setParameterReference(bremsVL_param);

		ParameterReference bremsVR_param = new ParameterReference();
		bremsVR_param.setServiceName(CAN_SERVICE_NAME);
		bremsVR_param.setName("bremsdruckVorneRechts");
		bremsVR_param.setReadSignal(bremsdruckVR);
		bremsdruckVR.setParameterReference(bremsVR_param);

		ParameterReference bremsHL_param = new ParameterReference();
		bremsHL_param.setServiceName(CAN_SERVICE_NAME);
		bremsHL_param.setName("bremsdruckHintenLinks");
		bremsHL_param.setReadSignal(bremsdruckHL);
		bremsdruckHL.setParameterReference(bremsHL_param);

		ParameterReference bremsHR_param = new ParameterReference();
		bremsHR_param.setServiceName(CAN_SERVICE_NAME);
		bremsHR_param.setName("bremsdruckHintenRechts");
		bremsHR_param.setReadSignal(bremsdruckHR);
		bremsdruckHR.setParameterReference(bremsHR_param);

		ParameterReference radGeschwVL_param = new ParameterReference();
		radGeschwVL_param.setServiceName(CAN_SERVICE_NAME);
		radGeschwVL_param.setName("radgeschwindigkeitVorneLinks");
		radGeschwVL_param.setReadSignal(radgeschwVL);
		radgeschwVL.setParameterReference(radGeschwVL_param);

		ParameterReference radGeschwVR_param = new ParameterReference();
		radGeschwVR_param.setServiceName(CAN_SERVICE_NAME);
		radGeschwVR_param.setName("radgeschwindigkeitVorneRechts");
		radGeschwVR_param.setReadSignal(radgeschwVR);
		radgeschwVR.setParameterReference(radGeschwVR_param);

		ParameterReference radGeschwHL_param = new ParameterReference();
		radGeschwHL_param.setServiceName(CAN_SERVICE_NAME);
		radGeschwHL_param.setName("radgeschwindigkeitHintenLinks");
		radGeschwHL_param.setReadSignal(radgeschwHL);
		radgeschwHL.setParameterReference(radGeschwHL_param);

		ParameterReference radGeschwHR_param = new ParameterReference();
		radGeschwHR_param.setServiceName(CAN_SERVICE_NAME);
		radGeschwHR_param.setName("radgeschwindigkeitHintenRechts");
		radGeschwHR_param.setReadSignal(radgeschwHR);
		radgeschwHR.setParameterReference(radGeschwHR_param);

		ParameterReference lenkwinkel_param = new ParameterReference();
		lenkwinkel_param.setServiceName(CAN_SERVICE_NAME);
		lenkwinkel_param.setName("lenkwinkel");
		lenkwinkel_param.setReadSignal(lenkwinkel);
		lenkwinkel.setParameterReference(lenkwinkel_param);

		ParameterReference lenkwinkelGeschw_param = new ParameterReference();
		lenkwinkelGeschw_param.setServiceName(CAN_SERVICE_NAME);
		lenkwinkelGeschw_param.setName("lenkwinkelgeschwindigkeit");
		lenkwinkelGeschw_param.setReadSignal(lenkwinkelgeschw);
		lenkwinkelgeschw.setParameterReference(lenkwinkelGeschw_param);

		ParameterReference fahrpedal_param = new ParameterReference();
		fahrpedal_param.setServiceName(CAN_SERVICE_NAME);
		fahrpedal_param.setName("fahrpedalstellung");
		fahrpedal_param.setReadSignal(fahrpedalst);
		fahrpedalst.setParameterReference(fahrpedal_param);

		ParameterReference bremspedal_param = new ParameterReference();
		bremspedal_param.setServiceName(CAN_SERVICE_NAME);
		bremspedal_param.setName("bremspedalstellung");
		bremspedal_param.setReadSignal(bremspedalst);
		bremspedalst.setParameterReference(bremspedal_param);

		ParameterReference leistungHV_param = new ParameterReference();
		leistungHV_param.setServiceName(CAN_SERVICE_NAME);
		leistungHV_param.setName("leistungHV");
		leistungHV_param.setReadSignal(leistungHV);
		leistungHV.setParameterReference(leistungHV_param);

		ParameterReference hochvolt_param = new ParameterReference();
		hochvolt_param.setServiceName(CAN_SERVICE_NAME);
		hochvolt_param.setName("hochvoltspannung");
		hochvolt_param.setReadSignal(hochvoltspannung);
		hochvoltspannung.setParameterReference(hochvolt_param);

		ParameterReference stromHV_param = new ParameterReference();
		stromHV_param.setServiceName(CAN_SERVICE_NAME);
		stromHV_param.setName("stromHV");
		stromHV_param.setReadSignal(stromHV);
		stromHV.setParameterReference(stromHV_param);

		ParameterReference hauptschuetz_param = new ParameterReference();
		hauptschuetz_param.setServiceName(CAN_SERVICE_NAME);
		hauptschuetz_param.setName("hauptschütz");
		hauptschuetz_param.setReadSignal(hauptschuetz);
		List<StateMapping> hauptsch_mappings = new LinkedList<StateMapping>();
		hauptsch_mappings.add(new StateMapping("Offen", 0D));
		hauptsch_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping mp : hauptsch_mappings) {
			mp.setParameter(hauptschuetz_param);
		}
		hauptschuetz_param.setStateMappings(hauptsch_mappings);
		hauptschuetz.setParameterReference(hauptschuetz_param);

		ParameterReference nvschutz_param = new ParameterReference();
		nvschutz_param.setServiceName(CAN_SERVICE_NAME);
		nvschutz_param.setName("NV-schütz");
		nvschutz_param.setReadSignal(nvSchuetz);
		List<StateMapping> nvschutz_mappings = new LinkedList<StateMapping>();
		nvschutz_mappings.add(new StateMapping("Offen", 0D));
		nvschutz_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping mp : nvschutz_mappings) {
			mp.setParameter(nvschutz_param);
		}
		nvschutz_param.setStateMappings(nvschutz_mappings);
		nvSchuetz.setParameterReference(nvschutz_param);

		ParameterReference ladeschuetz_param = new ParameterReference();
		ladeschuetz_param.setServiceName(CAN_SERVICE_NAME);
		ladeschuetz_param.setName("ladeschütz");
		ladeschuetz_param.setReadSignal(ladeschuetz);
		List<StateMapping> ladeschuetz_mappings = new LinkedList<StateMapping>();
		ladeschuetz_mappings.add(new StateMapping("Offen", 0D));
		ladeschuetz_mappings.add(new StateMapping("Geschlossen", 1D));
		for (StateMapping mp : ladeschuetz_mappings) {
			mp.setParameter(ladeschuetz_param);
		}
		ladeschuetz_param.setStateMappings(ladeschuetz_mappings);
		ladeschuetz.setParameterReference(ladeschuetz_param);

		ParameterReference leistungNV_param = new ParameterReference();
		leistungNV_param.setServiceName(CAN_SERVICE_NAME);
		leistungNV_param.setName("leistungNV");
		leistungNV_param.setReadSignal(leistungNV);
		leistungNV.setParameterReference(leistungNV_param);

		ParameterReference niedervolt_param = new ParameterReference();
		niedervolt_param.setServiceName(CAN_SERVICE_NAME);
		niedervolt_param.setName("niedervoltspannung");
		niedervolt_param.setReadSignal(niedervoltspannung);
		niedervoltspannung.setParameterReference(niedervolt_param);

		ParameterReference stromNV_param = new ParameterReference();
		stromNV_param.setServiceName(CAN_SERVICE_NAME);
		stromNV_param.setName("stromNV");
		stromNV_param.setReadSignal(stromNV);
		stromNV.setParameterReference(stromNV_param);

		ParameterReference kl30l_param = new ParameterReference();
		kl30l_param.setServiceName(CAN_SERVICE_NAME);
		kl30l_param.setName("kl30l");
		kl30l_param.setReadSignal(kl30l);
		kl30l.setParameterReference(kl30l_param);

		ParameterReference kl30b_param = new ParameterReference();
		kl30b_param.setServiceName(CAN_SERVICE_NAME);
		kl30b_param.setName("kl30b");
		kl30b_param.setReadSignal(kl30b);
		kl30b.setParameterReference(kl30b_param);

		ParameterReference kl15_param = new ParameterReference();
		kl15_param.setServiceName(CAN_SERVICE_NAME);
		kl15_param.setName("kl15");
		kl15_param.setReadSignal(kl15);
		kl15.setParameterReference(kl15_param);

		ParameterReference ucu_param = new ParameterReference();
		ucu_param.setServiceName(CAN_SERVICE_NAME);
		ucu_param.setName("ucu");
		ucu_param.setReadSignal(ucu);
		List<StateMapping> ucu_mappings = new LinkedList<StateMapping>();
		ucu_mappings.add(new StateMapping("Offline", 0D));
		ucu_mappings.add(new StateMapping("Initialisierung", 1D));
		ucu_mappings.add(new StateMapping("HV-aktiv", 2D));
		ucu_mappings.add(new StateMapping("Betrieb", 3D));
		ucu_mappings.add(new StateMapping("Shutdown", 4D));
		ucu_mappings.add(new StateMapping("TBD", 5D));
		for (StateMapping mp : ucu_mappings) {
			mp.setParameter(hauptschuetz_param);
		}
		ucu_param.setStateMappings(ucu_mappings);
		ucu.setParameterReference(ucu_param);

		ParameterReference tempHV_param = new ParameterReference();
		tempHV_param.setServiceName(CAN_SERVICE_NAME);
		tempHV_param.setName("temperaturHVAkku");
		tempHV_param.setReadSignal(tempAkku);
		tempAkku.setParameterReference(tempHV_param);

		ParameterReference tempGetriebe_param = new ParameterReference();
		tempGetriebe_param.setServiceName(CAN_SERVICE_NAME);
		tempGetriebe_param.setName("temperaturGetriebe");
		tempGetriebe_param.setReadSignal(tempGetriebe);
		tempGetriebe.setParameterReference(tempGetriebe_param);

		ParameterReference tempKuehl_param = new ParameterReference();
		tempKuehl_param.setServiceName(CAN_SERVICE_NAME);
		tempKuehl_param.setName("temperaturKuehlkreislauf");
		tempKuehl_param.setReadSignal(tempKuehl);
		tempKuehl.setParameterReference(tempKuehl_param);

		ParameterReference tempKuehl2_param = new ParameterReference();
		tempKuehl2_param.setServiceName(CAN_SERVICE_NAME);
		tempKuehl2_param.setName("temperaturKuehlkreislauf2");
		tempKuehl2_param.setReadSignal(tempKuehl2);
		tempKuehl2.setParameterReference(tempKuehl2_param);

		ParameterReference tempPeltier_param = new ParameterReference();
		tempPeltier_param.setServiceName(CAN_SERVICE_NAME);
		tempPeltier_param.setName("temperaturPeltierelemente");
		tempPeltier_param.setReadSignal(tempPeltier);
		tempPeltier.setParameterReference(tempPeltier_param);

		ParameterReference blinker = new ParameterReference();
		blinker.setServiceName(CAN_SERVICE_NAME);
		blinker.setName("blinker");
		blinker.setWriteSignal(sollBlinker);
		blinker.setReadSignal(blinker_disp);
		sollBlinker.setParameterReference(blinker);
		blinker_disp.setParameterReference(blinker);
		List<StateMapping> blinkerMappings = new LinkedList<StateMapping>();
		blinkerMappings.add(new StateMapping("Kein Blinken", 0D));
		blinkerMappings.add(new StateMapping("Blinker Links", 1D));
		blinkerMappings.add(new StateMapping("Blinker Rechts", 2D));
		blinkerMappings.add(new StateMapping("Warnblinker", 3D));
		for (StateMapping sm : blinkerMappings) {
			sm.setParameter(blinker);
		}
		blinker.setStateMappings(blinkerMappings);

		ParameterReference licht = new ParameterReference();
		licht.setServiceName(CAN_SERVICE_NAME);
		licht.setName("licht");
		licht.setWriteSignal(sollLicht);
		licht.setReadSignal(licht_disp);
		sollLicht.setParameterReference(licht);
		licht_disp.setParameterReference(licht);
		List<StateMapping> lichtMappings = new LinkedList<StateMapping>();
		lichtMappings.add(new StateMapping("Kein Licht", 0D));
		lichtMappings.add(new StateMapping("Standlicht", 1D));
		lichtMappings.add(new StateMapping("Tagfahrlicht", 2D));
		lichtMappings.add(new StateMapping("Abblendlicht", 3D));
		lichtMappings.add(new StateMapping("Fernlicht", 4D));
		for (StateMapping sm : lichtMappings) {
			sm.setParameter(licht);
		}
		licht.setStateMappings(lichtMappings);

		ParameterReference gang = new ParameterReference();
		gang.setServiceName(CAN_SERVICE_NAME);
		gang.setName("gang");
		gang.setWriteSignal(sollGang);
		gang.setReadSignal(gang_disp);
		sollGang.setParameterReference(gang);
		gang_disp.setParameterReference(gang);
		List<StateMapping> gangMappings = new LinkedList<StateMapping>();
		gangMappings.add(new StateMapping("Neutral", 0D));
		gangMappings.add(new StateMapping("Drive", 1D));
		gangMappings.add(new StateMapping("Reverse", 2D));
		gangMappings.add(new StateMapping("Park", 3D));
		gangMappings.add(new StateMapping("Error", 4D));
		gangMappings.add(new StateMapping("TBD", 5D));
		for (StateMapping sm : gangMappings) {
			sm.setParameter(gang);
		}
		gang.setStateMappings(gangMappings);

		ParameterReference hupe = new ParameterReference();
		hupe.setServiceName(CAN_SERVICE_NAME);
		hupe.setName("hupe");
		hupe.setWriteSignal(sollHupe);
		sollHupe.setParameterReference(hupe);

		ParameterReference ptt_param = new ParameterReference();
		ptt_param.setServiceName(CAN_SERVICE_NAME);
		ptt_param.setName("pushToTalk");
		ptt_param.setReadSignal(ptt);
		ptt.setParameterReference(ptt_param);

		ParameterReference parkbremse_param = new ParameterReference();
		parkbremse_param.setServiceName(CAN_SERVICE_NAME);
		parkbremse_param.setName("parkbremse");
		parkbremse_param.setReadSignal(pb_aktiv);
		pb_aktiv.setParameterReference(parkbremse_param);

		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		if (!entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().begin();
		}
		entityManager.persist(disp_ext);
		entityManager.persist(disp_ext_2);
		entityManager.persist(debSettingsDriver);
		entityManager.persist(clima_driver);
		entityManager.persist(abs);
		entityManager.persist(absdeaktivieren);
		entityManager.persist(esp);
		entityManager.persist(espdeaktivieren);
		entityManager.persist(safety);
		entityManager.persist(airbag_param);
		entityManager.persist(speedlimit_param);
		entityManager.persist(defrost_param);
		entityManager.persist(soc_param);
		entityManager.persist(energie_param);
		entityManager.persist(brenner_param);
		entityManager.persist(vSpeed_param);
		entityManager.persist(innen_param);
		entityManager.persist(aussen_param);
		entityManager.persist(kollassist);
		entityManager.persist(luefterInnen);
		entityManager.persist(klappe);
		entityManager.persist(tempvorgabe);
		entityManager.persist(klima);
		entityManager.persist(sitzKlima);
		entityManager.persist(sitzKlima2);
		entityManager.persist(umluft);
		entityManager.persist(tv);
		entityManager.persist(fahrmodus);
		entityManager.persist(longAcc_param);
		entityManager.persist(latAcc_param);
		entityManager.persist(yawRate_param);
		entityManager.persist(tvi_param);
		entityManager.persist(torque_value_param);
		entityManager.persist(inv_param);
		entityManager.persist(inv_torque_param);
		entityManager.persist(bremsVL_param);
		entityManager.persist(bremsVR_param);
		entityManager.persist(bremsHL_param);
		entityManager.persist(bremsHR_param);
		entityManager.persist(radGeschwVL_param);
		entityManager.persist(radGeschwVR_param);
		entityManager.persist(radGeschwHL_param);
		entityManager.persist(radGeschwHR_param);
		entityManager.persist(lenkwinkel_param);
		entityManager.persist(lenkwinkelGeschw_param);
		entityManager.persist(fahrpedal_param);
		entityManager.persist(bremspedal_param);
		entityManager.persist(leistungHV_param);
		entityManager.persist(hochvolt_param);
		entityManager.persist(stromHV_param);
		entityManager.persist(hauptschuetz_param);
		entityManager.persist(nvschutz_param);
		entityManager.persist(ladeschuetz_param);
		entityManager.persist(leistungNV_param);
		entityManager.persist(niedervolt_param);
		entityManager.persist(stromNV_param);
		entityManager.persist(kl30l_param);
		entityManager.persist(kl30b_param);
		entityManager.persist(kl15_param);
		entityManager.persist(ucu_param);
		entityManager.persist(tempHV_param);
		entityManager.persist(tempGetriebe_param);
		entityManager.persist(tempKuehl_param);
		entityManager.persist(tempKuehl2_param);
		entityManager.persist(tempPeltier_param);
		entityManager.persist(blinker);
		entityManager.persist(licht);
		entityManager.persist(gang);
		entityManager.persist(hupe);
		entityManager.persist(nebelschluss_param);
		entityManager.persist(volumeDown);
		entityManager.persist(volumePressed);
		entityManager.persist(volumeUp);
		entityManager.persist(shutdownRef);
		entityManager.persist(heckklappe_param);
		entityManager.persist(fronthaube_param);
		entityManager.persist(ptt_param);
		entityManager.persist(parkbremse_param);
        entityManager.persist(ladekabel_param);
		entityManager.getTransaction().commit();
		LOG.info("Initialized the CAN database...");
	}
}
