# Tutorial: Erstellung eines Dienstes auf dem Automotive Service Bus

Im Folgenden werden wir ein HTML-basiertes Dashboard entwickeln, das auf den Automotive Service Bus zugreift und Daten eines simulierten Fahrzeuges visualisiert. Das Dashboard zeigt die aktuelle Geschwindigkeit des Autos an und ermöglicht eine stufenwiese Verstellung der Lüfterstärke im Fahrzeug. Außerdem wird auf dem Dashboard die aktuelle Temperatur an einem vom Nutzer zu bestimmenden Ort angezeigt (zum Beispiel der Zielort der Fahrt). Über ein Menü kann der Ort ausgewählt werden, dessen Temperatur angezeigt wird. Das HTML-Dashboard wird über die REST-API des ASB auf die benötigten Daten zugreifen. Die aktuelle Geschwindigkeit sowie die Lüfterregelung sind bereits im ASB integriert. Um die Temperaturdaten zu erhalten werden wir uns eigenen Service bauen und diesen auf dem ASB installieren. Wir beginnen mit der Implementierung des Services und werden danach die HTML-Oberfläche erstellen. 
Wie eingangs erwähnt, basiert der ASB auf OSGi und dem OSGi-Container-Karaf. Wir werden uns im folgenden Tutorial auf eine knappe Einführung verschiedener Features von OSGi und Karaf beschränken, die für dieses Tutorial nötig sind. Für eine umfassende Einführung in OSGi und Karaf, die für die Entwicklung komplexerer Services hilfreich ist, empfehlen wir die Lektüre weiterführender Literatur:
http://karaf.apache.org/manual/latest/users-guide/index.html (User-Guide für Karaf)
http://www.osgi.org/Specifications/HomePage (Offizielle OSGi Spezifikation)

## Schritt 1 - Benötigte Software
- Java SDK Version 7
- Maven (Version 3.0.x, nicht höher und auch nicht niedriger), Link: https://maven.apache.org/download.cgi, Anweisungen zur Installation beachten
- Eclipse (mind. Version Luna)

## Schritt 2 - Konfiguration vor Entwicklung
- `JAVA_HOME` entsprechend setzen (Anleitung: http://www-01.ibm.com/support/knowledgecenter/SSPJLC_7.5.0/com.ibm.si.mpl.doc_7.5.0/install/t_set_java_home_variable.html?lang=de) 
- Git-Repository mit dem Source-Code des ASB von https://github.com/Mobility-Services-Lab/AutomotiveServiceBus klonen:
 - `git clone git@github.com:Mobility-Services-Lab/AutomotiveServiceBus.git`
 - Alternativ: Git-Repository als zip-Datei herunterladen und entpacken
- Im neu erstellten bzw. entpackten Ordner auf der Kommandozeile den Befehl `mvn install` ausführen. Hiermit werden der ASB, die dazugehörigen Komponenten und die API zur Programmierung kompiliert.

## Schritt 3 - Service-Projekt erstellen
- Projektskelett erstellen:
 - Es gibt einen Maven-Archetyp der ein komplettes einsatzbereites Beispielprojekt erstellt. Mit dem folgenden Befehl (in einem leeren Ordner auf der Kommandozeile ausführen) wird der Archetype angesteuert und das Projekt erstellt, folgende Werte im Befehl sollten angepasst werden:
   - `-DgroupId`: Präfix `de.visiom.carpc.services` + Name der Komponente, z.B. `de.visiom.carpc.services.weather`
   - `-DartifactId`: Name der Kompenente + Suffix `-parent`, z.B. `weather-parent`
   - `-Dpackage`: Gleicher Wert wie `-DgroupId`
   - `-DserviceName`: Name der Komponente, z.B. `weather`
 - Befehl zum Ausführen, bereits angepasst:
`mvn archetype:generate -DarchetypeGroupId=de.visiom.carpc -DarchetypeArtifactId=service-archetype -DarchetypeVersion=1.1.0 -DgroupId=de.visiom.carpc.services.weather -DartifactId=weather-parent -Dversion=1.0.0-SNAPSHOT -Dpackage=de.visiom.carpc.services.weather -DinteractiveMode=false -DserviceName=weather`
- Projekt in Eclipse einbinden:
 - Links bei Projekten „Import“, dann „Maven“, dann „Existing Maven Projects“
 - In den Ordner wechseln in dem das obige Maven-Kommando ausgeführt wurde, alle Projekte auswählen und auf „Finish“ => Evtl. auftretende Fehler können ignoriert werden

## Schritt 4 - Service implementieren
### Schritt 4.1 - Metadaten des Services definieren
- Jeder Service verwaltet eine Reihe an Werten, im folgenden Parameter genannt. Im ASB fest installiert sind bereits die Services „Auto“ und „Klima“, die Parameter wie „Geschwindigkeit“ oder „Lüfterstärke“ verwalten. Services senden Nachrichten über einen Nachrichtenbus, wenn sich der Wert eines Parameters ändert. Andere Services können nach dem Publish-Subscribe-Prinzip Änderungen für einen bestimmten Parameter abonnieren. Außerdem können sie auch selber Anfragen auf den Nachrichtenbus legen, wenn sie möchten dass ein Service den Wert einer seiner Parameter ändert.
- Die Schnittstelle zu externen Systemen, wie beispielsweise Benutzeroberflächen, bildet eine REST-API. Services können definieren, welche ihrer Parameter sie über die REST-API freigeben möchten.
- Im Folgenden implementieren wir unseren Service, der als Parameter die aktuelle Temperatur an einem gewählten Ort sowie diesen Ort selber verwaltet.
- Zunächst muss definiert werden, welche Parameter bzw. Werte der Service zum Lesen/Schreiben über den Bus freigibt => Definierbar über XML-Dateien, diese Dateien benutzt dann der ASB um bei Schreibzugriffen auf den Nachrichtenbus zu überprüfen, ob die zu schreibenden Werte gültig sind, außerdem wird daraus automatisch die REST-API generiert
- Im Projekt „service“ die Datei src/main/resources/OSGI-INF/service.xml öffnen
- Den Beispieleintrag durch den folgenden Code ersetzen:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<service name="weather" description="">
	<parameters>
		<numericParameter id="0" name="remoteTemperature" range="-50..50" stepSize="0.01" unitSymbol="°C" readOnly="true" />
		<stateParameter id="1" name="remoteLocation">
			<states>
				<state>Berlin</state>
				<state>Garching</state>
				<state>Bonn</state>
			</states>
		</stateParameter>
	</parameters>
</service>
```
- Im Tag `<parameters>` geben wir alle Parameter an, die wir definieren möchten. Jedem Parameter wird ein fester Datentyp zugeordnet, außerdem können noch weitere Metadaten angegeben werden. Den Datentyp legen wir über die Wahl des XML-Tags fest, dabei gibt es folgende Möglichkeiten:
 - `<numericParameter>` für numerische (Fließkomma-)Werte (z.B. Geschwindigkeit, Temperatur, …)
 - `<switchParameter>` für Boolean-Werte (z.B. Tür offen/zu, ABS an/aus, …)
 - `<stringParameter>` für Textwerte (z.B. Name des aktuellen Radiosenders, …)
 - `<stateParameter>` für Textwerte mit fest vordefinierten Werten (z.B. Fahrmodus: Default, Eco, Comfort, Sport, …)
- Über die Attribute des jeweiligen Tag können abhängig vom Datentyp weitere Metadaten angegeben werden, für numerische Parameter kann beispielsweise der Definitionsbereich oder die Schrittweite definiert werden. Außerdem kann für alle Datentypen angegeben werden, ob der jeweilige Parameter „read-only“ ist, also ob der dazugehörige Wert nur vom zugeordneten Service verändert werden kann
 - `remoteTemperature` ist der Parameter für die Temperatur an unserem Ort, wir definieren dies als `numericParameter`, da es sich um einen numerischen Wert handelt, über die Attribute geben wir die passenden Metadaten an:
   - `range=“-50..50“` => Das darstellbare Minimum für diesen Parameter ist -50, das Maximum +50
   - `stepSize=“0.01“` => Es sind nur Werte mit der Schrittweite 0.01 darstellbar, wenn kleinere Werte auf den Bus gelegt werden werden diese vor dem Senden gerundet
   - `unitSymbol=“°C“` => In String-Darstellungen wird der Text „°C“ an den Wert angehängt
   - `readOnly` ist auf `true` gesetzt, d.h. der Wert kann nur vom Service selber geändert werden und nicht von externen Systemen => Sinnvoll, da sonst der Fahrer die Temperatur „verfälschen“ könnte
 - `remoteLocation` ist der Parameter für den aktuellen Ort, dessen Temperatur berechnet werden soll, wir definieren dies als StateParameter da der Fahrer bzw. Benutzer nur aus einer vorgegebenen Auswahl an Orten auswählen können soll
   - `readOnly=“false“` => Von externen Systemen, z.B. Fährer änderbar => der Fahrer kann aus einer vorgegebenen Menge an Orten einen Wert auswählen
- Im Projekt `service` die Datei `src/main/resources/OSGI-INF/rest/service.xml` öffnen => Aus dieser Datei wird die REST-API generiert, hier wird also angegeben welche Parameter über die API zugänglich sind und wie diese von der API behandelt werden
- Den Beispieleintrag durch folgenden Code ersetzen, dadurch wird definiert, wie die zuvor definierten Parameter in der REST-API abgebildet werden:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<service path="weather" name="weather" ranking="10">
	<parameters>
		<restNumericParameter path="remoteTemperature" referenceParameter="0" name="Remote Temperature" stepSize="0.1" readOnly="true" />
		<restStateParameter path="remoteLocation" referenceParameter="1" name="Remote Location" />
	</parameters>
</service>
```
- Für jeden Parameter, der in der REST-API freigegeben werden soll, muss hier also ein entsprechender Eintrag mit dem identischen Datentyp und dem Präfix `rest` angelegt werden, über die Attribute können wieder Metadaten definiert werden
- Eine Erklärung der hier verwendeten Attribute:
 - `path`: Letzter Teil der URL, über die auf den Parameter mit der REST-API zugegriffen werden kann => generelles URL-Schema: `http://SERVER_ADRESS:8080/services/SERVICE_NAME/parameters/PARAMETER_NAME`
 - `referenceParameter`: Die ID des Parameters (siehe `OSGI-INF/service.xml`) der hier abgebildet wird
 - `name`: Verständlicher Name des Parameters, wird von der REST-API zurückgegeben und kann z.B. direkt in GUIs benutzt werden
 - Für numerische Parameter können wieder Beschränkungen angegeben werden – in diesem Fall wird nur die stepSize weiter verringert, da in UIs Temperaturwerte meist nur auf eine Nachkommastelle genau angezeigt werden => auch hier werden kleinere Werte entsprechend gerundet

### Schritt 4.2 - Service-Logik implementieren
- Als nächstes wird im Projekt `impl` das Verhalten des Services definiert => wie geht der Service mit externen Änderungen von Parameterwerten um, wann veröffentlicht der Service selber Änderungen etc.
- Die bereits vordefinierten Klassen `ParameterUpdateHandler` und `ParameterUpdatePublisher` können wir löschen, diese werden wir für unseren Service nicht benötigen
- In unserem Code werden wir außerdem via Maven zwei externe Bibliotheken einbinden und benutzen: MinimalJSON und Jersey um einen externen Wetterdienst-Service via HTTP anzusprechen und dessen JSON-Antwort zu parsen. Um diese Bibliotheken beim Kompilieren verwenden zu können, müssen wir in der Maven-Konfigurationsdatei `pom.xml` bei der Angabe der Dependencies (Tag `<dependencies>`) folgende Dependencies hinzufügen:
```xml
		<dependency>
			<groupId>org.glassfish.jersey.core</groupId>
			<artifactId>jersey-client</artifactId>
			<version>2.5</version>
		</dependency>
		<dependency>
			<groupId>com.eclipsesource.minimal-json</groupId>
			<artifactId>minimal-json</artifactId>
			<version>0.9.1</version>
		</dependency>
```
- Wir müssen außerdem sicherstellen, dass diese Bibliotheken auch später zur Laufzeit zur Verfügung stehen. Hierfür öffnen wir kurz das Projekt `features` und betrachten die Datei `src/main/feature/feature.xml`. Nach der Zeile `<feature>asb</feature>` fügen wir folgende Zeilen ein:
```xml
        <bundle start-level="85">mvn:com.eclipsesource.minimal-json/minimal-json/0.9.1</bundle>
        <bundle start-level="85">mvn:org.glassfish.jersey.core/jersey-client/2.5</bundle>
```
- Hiermit wird dem ASB-Framework mitgeteilt, dass diese Bibliotheken vor Aktivierung des Services bereitgestellt sein müssen – das Framework kümmert sich selbst um das Downloaden und Installieren in die Laufzeitumgebung, wir müssen lediglich in dieser Datei alle Bibliotheken angeben die wir benötigen.
- Nun zurück zum Projekt `impl` und zur ersten Fragestellung: Wie reagiert der Service, wenn ein externer Benutzer den Parameter remoteLocation über die REST-API ändert?
 - Package `de.visiom.carpc.services.weather.handlers` anlegen, Klasse `RemoteLocationChangeRequestHandler` erstellen:

```java
package de.visiom.carpc.services.weather.handlers;

import de.visiom.carpc.asb.messagebus.CommandPublisher;
import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.messagebus.commands.GenericResponse;
import de.visiom.carpc.asb.messagebus.commands.Response;
import de.visiom.carpc.asb.messagebus.commands.ValueChangeRequest;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.messagebus.handlers.ValueChangeRequestHandler;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.StateValueObject;

public class RemoteLocationChangeRequestHandler extends
		ValueChangeRequestHandler {

	private CommandPublisher commandPublisher;

	private EventPublisher eventPublisher;

	public void setCommandPublisher(CommandPublisher commandPublisher) {
		this.commandPublisher = commandPublisher;
	}

	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void onValueChangeRequest(ValueChangeRequest request, Long requestID) {
		Parameter parameter = request.getParameter();
		Response response = null;
		if (parameter.getName().equals("remoteLocation")) {
			StateValueObject valueObject = (StateValueObject) request
					.getValue();
			ValueChangeEvent valueChangeEvent = ValueChangeEvent
					.createValueChangeEvent(parameter, valueObject);
			eventPublisher.publishValueChange(valueChangeEvent);
			response = GenericResponse
					.createGenericResponse(GenericResponse.STATUS_OK);
		} else {
			response = GenericResponse
					.createGenericResponse(GenericResponse.STATUS_ERROR);
		}
		commandPublisher.publishResponse(response, requestID, request
				.getParameter().getService());
	}

}
```
 - Die Methode `onValueChangeRequest` wird vom Framework aufgerufen, sobald ein PUT-Request an die REST-API gesendet wird und sich der Wert vom aktuellen Wert des Parameters unterscheidet
 - Damit nicht bei jedem beliebigen Request für alle verfügbaren Services diese Methode aufgerufen wird, kann für jeden `RequestHandler` konfiguriert werden, auf welche Requests welches Services er horcht => wird später eingestellt
 - Über das `ValueChangeRequest`-Argument können die Details des Requests ausgelesen werden => `getParameter()` liefert das passende Parameter-Objekt mit Details wie z.B. Name, Beschreibung und weitere in der `service.xml` festgelegte Metadaten
 - `getValue()` liefert ein Objekt zurück, das das Interface `ValueObject` implementiert – für jeden Parametertyp gibt es passende Unterklassen, z.B. `NumberValueObject` für numerische Parameter oder `StateValueObject` für `StateParameter`
 - In unserer Beispielklasse wird nur überprüft, ob der Parameter `remoteLocation` heißt – prinzipiell könnte es noch weitere Parameter geben, wir möchten aber nur diesen spezifischen Parameter in unserer Klasse behandeln
 - Anschließend wird ein `ValueChangeEvent` generiert und über den `EventPublisher` auf den Nachrichtenbus gelegt – hiermit signalisiert der Service, dass sich der Wert des Parameters geändert hat, alle anderen Services die Änderungen dieses Parameters abonniert haben erhalten nun eine Nachricht
 - Zuletzt wird über den `CommandPublisher` eine Antwort an den Sender des Requests geschickt (intern ebenfalls über den Nachrichtenbus), in unserem Fall ist diese positiv
 - In komplexeren Fällen können vor dem Senden des `ValueChangeEvents` noch umfangreichere Prüfungen stattfinden und abhängig davon der `ValueChangeRequest` ggf. auch abgelehnt werden (durch das Senden einer negativen Antwort)
 - In dieser Klasse verwenden wir Objekte vom Typ `EventPublisher` und `CommandPublisher` – diese stellen unsere Schnittstelle zum Nachrichtenbus dar und sind Teil der ASB-API. Wir instantiieren diese Klassen nicht selber sondern lassen dies einen Dependency-Injection-Mechanismus erledigen, den wir später noch konfigurieren werden
- Bei der soeben angelegten Klasse handelte es sich um einen `ValueChangeRequestHandler`. Diese sind zuständig für `ValueChangeRequests`, also Nachrichten von anderen Services, die den Service des Handlers auffordern, den Wert einer seiner Parameter zu ändern (in diesem Fall fordert der REST-Service unseren Service auf, den Wert des `remoteLocation`-Parameters zu ändern)
- Der ASB kennt noch eine weitere Handler-Klasse: `ValueChangeEventHandler`. Diese können `ValueChangeEvents` für Parameter bestimmter Services abonnieren, sie werden also aufgerufen wenn ein Service signalisiert dass sich der Wert einer seiner Parameter geändert hat. Prinzipiell würde uns unser `ValueChangeRequestHandler` ausreichen, da wir in diesem den neuen Wert bereits auslesen können, zu Demonstrationszwecken werden wir nun aber noch einen `ValueChangeEventHandler` schreiben, der auf das soeben gesendete `ValueChangeEvent` reagiert und die nächsten Schritte zur Neuberechnung der Temperatur anstößt
 - Im Package `de.visiom.carpc.services.weather.handlers` die Klasse `RemoteLocationUpdateHandler` anlegen (die auftretenden Fehler werden im weiteren Verlauf des Tutorials gelöst):
```java
 package de.visiom.carpc.services.weather.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.messagebus.handlers.ValueChangeEventHandler;
import de.visiom.carpc.asb.servicemodel.parameters.Parameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.StateValueObject;
import de.visiom.carpc.services.weather.publishers.RemoteTemperaturePublisher;

public class RemoteLocationUpdateHandler extends ValueChangeEventHandler {

	private static final Logger LOG = LoggerFactory
			.getLogger(RemoteLocationUpdateHandler.class);

	private RemoteTemperaturePublisher remoteTemperaturePublisher;

	public void setRemoteTemperaturePublisher(
			RemoteTemperaturePublisher remoteTemperaturePublisher) {
		this.remoteTemperaturePublisher = remoteTemperaturePublisher;
	}

	@Override
	public void onValueChangeEvent(ValueChangeEvent valueChangeEvent) {
		StateValueObject stateValueObject = (StateValueObject) valueChangeEvent
				.getValue();
		Parameter parameter = valueChangeEvent.getParameter();
		String value = stateValueObject.getValue();
		LOG.info("Received an update for {}/{}: {}", parameter.getName(),
				parameter.getService().getName(), value);
		remoteTemperaturePublisher.setLocation(value);
	}
}
```
 - Die Methode `onValueChangeEvent` wird aufgerufen, sobald sich der Wert eines Parameters ändert welcher von diesem Handler abonniert wurde
 - In unserem Beispiel schreibt der Handler lediglich eine kurze Nachricht in das Logfile und teilt dem `RemoteTemperaturePublisher` die Änderung mit
- Als nächstes wird der `RemoteTemperaturPublisher` implementiert, dieser ist dafür verantwortlich die aktuelle Temperatur zu berechnen und sie auf den Bus zu legen, damit wird auch automatisch ein Update über die REST-Schnittstelle geschickt
 - Package `de.visiom.carpc.services.weather.helpers` anlegen, Klassen `LocationCoordinatesMapper`, `ServiceConstants` und `WeatherClient` anlegen
```java
package de.visiom.carpc.services.weather.helpers;

import java.util.HashMap;
import java.util.Map;

public class LocationCoordinatesMapper {

	private Double currentLatitude = 0.0;
	
	private Double currentLongitude = 0.0;
	
	static Map<String, Double> latitudeData = new HashMap<String, Double>();
	
	static Map<String, Double> longitudeData = new HashMap<String, Double>();
	
	static {
		latitudeData.put("Garching", 48.2513878);
		longitudeData.put("Garching", 11.6509662);		
		latitudeData.put("Berlin", 52.5170365);
		longitudeData.put("Berlin", 13.3888599);
		latitudeData.put("Bonn", 50.7358511);
		longitudeData.put("Bonn", 7.1006599);
	}
	
	public void initialize(String locationName) {
		if (latitudeData.containsKey(locationName)) {
			currentLatitude = latitudeData.get(locationName);
		} else {
			currentLatitude = 0.0;
		}
		if (longitudeData.containsKey(locationName)) {
			currentLongitude = longitudeData.get(locationName);
		} else {
			currentLongitude = 0.0;
		}
	}
	
	public Double getLatitude() {
		return currentLatitude;
	}
	
	public Double getLongitude() {
		return currentLongitude;
	}
	
}
```
```java
package de.visiom.carpc.services.weather.helpers;

public class ServiceConstants {
    public static final String SERVICE_NAME = "weather";
    public static final String NO_SUCH_SERVICE_MESSAGE = "The service could not be loaded:";
    public static final String NO_SUCH_PARAMETER_MESSAGE = "The parameter could not be loaded:";
    public static final String UNINITIALIZED_PARAMETER_MESSAGE = "The requested parameter has not been initialized yet!";
}
```
```java
package de.visiom.carpc.services.weather.helpers;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.eclipsesource.json.JsonObject;

/**
 * Helper class that sends requests to openweathermap.org
 * 
 */
public class WeatherClient {

    private final WebTarget webTarget = ClientBuilder.newClient()
            .target("http://api.openweathermap.org").path("data/2.5/weather")
            .queryParam("units", "metric");

    private double latitude;

    private double longitude;

    public WeatherClient(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTemperature() {
        String output = webTarget.queryParam("lat", latitude)
                .queryParam("lon", longitude).request().get(String.class);
        return getTemperatureForJSONResponse(output);
    }

    private Double getTemperatureForJSONResponse(String jsonString) {
        JsonObject jsonObject = JsonObject.readFrom(jsonString);
        JsonObject main = jsonObject.get("main").asObject();
        return main.get("temp").asDouble();
    }

}
```
 - Dies sind Hilfsklassen, die uns dabei helfen, Ortsnamen in Koordinaten zu übersetzen und einen externen Webservice ansprechen, um die aktuelle Temperatur an bestimmten Koordinaten zu ermitteln
Nun implementieren wir die eigentliche Publisher-Klasse: Package `de.visiom.carpc.services.weather.publishers` anlegen, Klasse `RemoteTemperaturePublisher` erstellen:

```java
package de.visiom.carpc.services.weather.publishers;

import de.visiom.carpc.asb.messagebus.EventPublisher;
import de.visiom.carpc.asb.messagebus.async.ParallelWorker;
import de.visiom.carpc.asb.messagebus.events.ValueChangeEvent;
import de.visiom.carpc.asb.parametervalueregistry.exceptions.UninitalizedValueException;
import de.visiom.carpc.asb.servicemodel.Service;
import de.visiom.carpc.asb.servicemodel.exceptions.NoSuchParameterException;
import de.visiom.carpc.asb.servicemodel.parameters.NumericParameter;
import de.visiom.carpc.asb.servicemodel.parameters.StateParameter;
import de.visiom.carpc.asb.servicemodel.valueobjects.NumberValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.StateValueObject;
import de.visiom.carpc.asb.servicemodel.valueobjects.ValueObject;
import de.visiom.carpc.asb.serviceregistry.ServiceRegistry;
import de.visiom.carpc.asb.serviceregistry.exceptions.NoSuchServiceException;
import de.visiom.carpc.services.weather.helpers.LocationCoordinatesMapper;
import de.visiom.carpc.services.weather.helpers.ServiceConstants;
import de.visiom.carpc.services.weather.helpers.WeatherClient;

public class RemoteTemperaturePublisher extends ParallelWorker {

	private NumericParameter remoteTemperatureParameter;

	private EventPublisher eventPublisher;

	private ServiceRegistry serviceRegistry;

	private LocationCoordinatesMapper locationCoordinatesMapper;

	private WeatherClient weatherClient;

	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	private Double getCurrentLatitude() {
		return locationCoordinatesMapper.getLatitude();
	}

	private Double getCurrentLongitude() {
		return locationCoordinatesMapper.getLongitude();
	}

	@Override
	public void initialize() throws NoSuchParameterException,
			NoSuchServiceException, UninitalizedValueException {
		locationCoordinatesMapper = new LocationCoordinatesMapper();
		Service thisService = serviceRegistry
				.getService(ServiceConstants.SERVICE_NAME);
		remoteTemperatureParameter = (NumericParameter) thisService
				.getParameter("remoteTemperature");
		StateValueObject initialRemoteLocationValue = initializeRemoteLocation(thisService);
		locationCoordinatesMapper.initialize(initialRemoteLocationValue
				.toString());
		weatherClient = new WeatherClient(getCurrentLatitude(),
				getCurrentLongitude());
	}

	@Override
	public long getExecutionInterval() {
		return 10000;
	}

	@Override
	public void execute() {
		fireUpdate();
	}
	
	public void fireUpdate() {
		weatherClient.setLatitude(getCurrentLatitude());
		weatherClient.setLongitude(getCurrentLongitude());
		ValueObject valueObject = NumberValueObject.valueOf(weatherClient
				.getTemperature());
		ValueChangeEvent valueChangeEvent = ValueChangeEvent
				.createValueChangeEvent(remoteTemperatureParameter, valueObject);
		eventPublisher.publishValueChange(valueChangeEvent);
	}

	public StateValueObject initializeRemoteLocation(Service thisService)
			throws NoSuchParameterException {
		StateParameter initialLocation = (StateParameter) thisService
				.getParameter("remoteLocation");
		StateValueObject initialLocationValue = StateValueObject
				.valueOf("Garching");
		ValueChangeEvent valueChangeEvent = ValueChangeEvent
				.createValueChangeEvent(initialLocation, initialLocationValue);
		eventPublisher.publishValueChange(valueChangeEvent);
		return initialLocationValue;
	}

	public void setLocation(String locationName) {
		locationCoordinatesMapper.initialize(locationName);
		fireUpdate();
	}

}
```
 - Sobald der Service gestartet wird, wird die Methode `initialize()` aufgerufen, diese nutzen wir um unsere Hilfsklassen zu initialisieren und einen Default-Wert für `remoteLocation` zu setzen 
 - Als nächstes wird vom Framework in einer Endlosschleife die Methode `execute()` aufgerufen, diese nutzen wir, um über unseren `WeatherClient` die aktuelle Temperatur zu ermitteln, ein passendes `ValueChangeEvent` zu erstellen und dieses auf den Bus zu schreiben. Mithilfe der Methode `getExecutionInterval` können wir angeben, wieviel Zeit (in Millisekunden) zwischen zwei Aufrufen der Methode `execute()` vergehen soll – in unserem Beispiel wird also nach dem Beenden von `execute()` 10 Sekunden gewartet, bis die Methode erneut aufgerufen wird
 - Zum Schluss betrachten wir noch die Methode `setLocation()`, die bei einem Ortswechsel vom `RemoteLocationUpdateHandler` aufgerufen wird – wie wir sehen, wird hier sofort ein Update auf den Bus gelegt, damit Clients nach einem Ortwechsel nicht im schlimmsten Fall noch 10 Sekunden auf die Temperatur am neu gewählten Ort warten müssen
- Es gibt verschiedene Objekte, die Referenzen auf andere Objekte benötigen, so braucht z.B. der `RemoteLocationUpdateHandler` eine Referenz auf den `RemoteTemperaturePublisher`. Diese Referenzen können über das Dependency-Injection-Framework „Blueprint“ „verdrahtet“ werden, dies geschieht im `impl`-Projekt in der Datei `src/main/resources/OSGI-INF/blueprint/blueprint.xml`
- Wir löschen zunächst den bisherigen Inhalt der Datei und fügen folgende Code ein:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blueprint default-activation="eager"
	xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">

	<reference id="serviceRegistry" interface="de.visiom.carpc.asb.serviceregistry.ServiceRegistry" />

	<reference id="eventAdmin" interface="org.osgi.service.event.EventAdmin" />

	<reference id="eventPublisher" interface="de.visiom.carpc.asb.messagebus.EventPublisher" />

	<reference id="commandPublisher" interface="de.visiom.carpc.asb.messagebus.CommandPublisher" />

	<bean id="remoteTemperaturePublisher" class="de.visiom.carpc.services.weather.publishers.RemoteTemperaturePublisher" init-method="start">
		<property name="eventPublisher" ref="eventPublisher" />
		<property name="serviceRegistry" ref="serviceRegistry" />
	</bean>

	<bean id="locationChangeRequestHandler" class="de.visiom.carpc.services.weather.handlers.RemoteLocationChangeRequestHandler">
		<property name="eventPublisher" ref="eventPublisher" />
		<property name="commandPublisher" ref="commandPublisher" />
	</bean>

	<bean id="locationUpdateHandler" class="de.visiom.carpc.services.weather.handlers.RemoteLocationUpdateHandler">
		<property name="remoteTemperaturePublisher" ref="remoteTemperaturePublisher" />
	</bean>
	
	<service id="requestHandler" interface="org.osgi.service.event.EventHandler" ref="locationChangeRequestHandler">
		<service-properties>
			<entry key="event.topics" value="visiom/commands/requests" />
			<entry key="event.filter" value="(serviceName=weather)" />
		</service-properties>
	</service>

	<service id="updateHandler" interface="org.osgi.service.event.EventHandler"
		ref="locationUpdateHandler">
		<service-properties>
			<entry key="event.topics" value="visiom/updates/weather" />
			<entry key="event.filter" value="(parameterName=remoteLocation)" />
		</service-properties>
	</service>

</blueprint>
```
 - Objekte, die eine Referenz benötigen oder selber in ein anderes Objekt als Referenz „injected“ werden sollen, werden als „Bean“ deklariert, dadurch wird die Klasse zum Start des Services automatisch instanziiert sowie alle Referenzen befüllt, die in den `<property>`-Tags angegeben sind
 - Referenzen auf Objekte, die nicht in selbst definierten Services definiert sind sondern vom ASB bereitgestellt werden (z.B. der `EventPublisher`), können über den Tag `<reference>` importiert werden
 - Außerdem kann hier angegeben werden, welche Parameteränderungen von unseren Update- bzw. RequestHandlern abonniert werden sollen
  - Jeder Handler wird muss zunächst in einem `<service>`-Tag angegeben werden
  - Der ASB verwendet ein topic-basiertes Publish-Subscribe-Pattern mit Filtermechanismus, das Topic sowie Filter-Angaben können über `<service-properties>` definiert werden
  - Das Standard-Topic für Requests lautet `visiom/commands/requests`, für Event-Updates lautet es `visiom/updates/weather` => das passende Topic wird über die Property `event.topics` angegeben
  - Über die `event.filter`-Properties können Filter angegeben werden – in unserem Beispiel kann somit der `UpdateHandler` so konfiguriert werden, dass er nur bei Updates die den Parameter `remoteLocation` betreffen aufgerufen wird
- Zuletzt passen wir noch den Namen der Bundles unseres Services an, damit wir diese beim Debugging besser identifizieren können
 - Hierfür öffnen wir die `pom.xml`-Dateien in den Projekten `features`, `service` und `impl`
 - Der Name des Bundles wird im Tag `<name>` definiert, im `impl`-Projekt steht dort beispielsweise: `<name>CarPC :: Custom Service :: Implementation</name>`
 - Das Wort „Custom“ ersetzen wir in allen Dateien in diesem Tag durch das Wort „Weather“
 
### Schritt 4.3 - Service in ASB-Framework einbinden:
- Service mit `mvn clean install` (Kommandozeile im Parent-Ordner `weather-parent` des Projektes) kompilieren
- Im Git-Repository das in Schritt 3 (Konfiguration vor Entwicklung) gecloned wurde: Die Zip-Datei `carPC-assemblies/core-can-test-assembly/target/core-can-test-assembly-1.1.1-SNAPSHOT.zip` entpacken, hier handelt es sich um den ausführbaren ASB-Server
- Im entpackten Ordner die Datei `bin/karaf.bat` ausführen und somit den Server starten
- Die Datei `feature.xml` im `features`-Sub-Projekt des oben erstellten Projekts suchen (Pfad: `weather-parent\features\target\feature\feature.xml`), den Text im `<repository/>`-Tag kopieren (`mvn:de.visiom.carpc.services.weather/features/${project.version}/xml/features`)
- Die Karaf-Konsole öffnen und folgendes Kommando eingeben:
`feature:repo-add REPOSITORY` (REPOSITORY mit dem soeben kopierten Text ersetzen)
- Den Namen des features aus der `feature.xml` kopieren ("weather") und folgendes Kommando in der Karaf-Konsole eingeben:
`feature:install NAME` (NAME mit dem soeben kopierten Text ersetzen)
Wenn die Installation erfolgreich war, erscheint ein neuer Konsolenprompt
- Auch wenn die Installation erfolgreich war, können noch Laufzeitfehler auftreten, die meist auf Fehler in der Programmierung des Services zurückzuführen sind:
 - Der Befehl `la` in der Karaf-Konsole zeigt eine Liste aller installierten Bundles, die Bundles des soeben installierten Services befinden sich ganz unten.
 - Wenn bei allen Bundles links `Active` steht, war die Installation erfolgreich.
 - `Failed` bedeutet, dass ein Laufzeitfehler aufgetreten ist. Um diesen zu finden, sollten die Log-Nachrichten in der Datei `data/log/karaf.log` und `data/log/visiom.log` untersucht werden
 - Bei sämtlichen Änderungen am Service müssen die Punkte unter Schritt 4.3 neu durchgeführt werden, lediglich der 2. Und 3. Punkte (Herunterladen und Starten des Servers) können entfallen.

### Schritt 5 - Benutzeroberfläche erstellen
- Als nächstes implementieren wir eine HTML-basierte GUI, die die REST-API des ASB verwendet um mit dem Service zu interagieren den wir soeben geschrieben haben sowie um auf autointerne Werte zuzugreifen, die ebenfalls über den ASB gesendet werden
- Unsere HTML-Datei `index.html` hierfür sieht folgendermaßen aus:
```html
<html>
<head>
  <meta charset="UTF-8">
  <link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="style.css" rel="stylesheet"/>
  <script src="http://cdn.jsdelivr.net/raphael/2.1.2/raphael-min.js"></script>
  <script src="justgage.js"></script>
  <script src="https://code.jquery.com/jquery-2.1.3.js"></script>
  <script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
</head>
<body>
<div class="container-fluid">
  <div class="row text-center">
    <div class="col-md-6">
      <div class="box">
        <div id="gauge" style="width:400px; height:320px"></div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="box">
        Ausgewählte Stadt (Zielort): 
        <div id="city-selection">
          <select>
            <option value="Garching" selected>Garching</option>
            <option value="Berlin">Berlin</option>
            <option value="Bonn">Bonn</option>
          </select>
        </div>
      </div>
    </div>
  </div>
  <div class="row text-center">
    <div class="col-md-6">
      <div class="box">
        <label for="spinner">Innenraumlüfter:</label>
        <input id="spinner" name="value">
      </div>
    </div>
	<div class="col-md-6">
      <div class="box">
        Temperatur in <span id="city">Garching</span>: <span id="temp"></span>°C
      </div>
    </div>
    
  </div>
</div>
<script>
var URLluefterInnenraum = "http://localhost:8080/services/klima/parameters/luefterInnenraum";
var URLremoteLocation = "http://localhost:8080/services/weather/parameters/remoteLocation";
var URLgeschwindigkeit = "http://localhost:8080/services/auto/parameters/geschwindigkeit";
var URLremoteTemperature = "http://localhost:8080/services/weather/parameters/remoteTemperature";
var subscriptionSuffix = "/subscription";

function initializeValues(gauge) {
    $.ajax({
        url: URLremoteTemperature,
        success: function(data) {
            $("#temp").text(data.toFixed(1));
        }
    });
    $.ajax({
        url: URLgeschwindigkeit,
        success: function(data) {
            gauge.refresh(Math.abs(data));
        }
    });
    $.ajax({
        url: URLremoteLocation,
        success: function(data) {
            $("#city").text(data);
        }
    });
    $.ajax({
        url: URLluefterInnenraum,
        success: function(data) {
            $("#spinner").spinner("value", data);
        }
    });
};

function initializeSubscriptions(gauge) {
    var velocitySource = new EventSource(URLgeschwindigkeit + subscriptionSuffix);
    velocitySource.onmessage = function(event) {
        gauge.refresh(Math.abs(event.data))
    };

    var temperatureSource = new EventSource(URLremoteTemperature + subscriptionSuffix);
    temperatureSource.onmessage = function(event) {
        $("#temp").text(parseFloat(event.data).toFixed(1));
    };

    var citySource = new EventSource(URLremoteLocation + subscriptionSuffix);
    citySource.onmessage = function(event) {
        $("#city").text(event.data);
    };
}

function initializeWidgets() {
    $("#spinner").spinner({
        spin: function(event, ui) {
            var data = ui.value;
            $.ajax({
                url: URLluefterInnenraum,
                method: "PUT",
                data: data + ".0",
                success: function(data) {}
            });
        },
        min: 0,
        max: 10
    });

    var gauge = new JustGage({
        id: "gauge",
        value: 0,
        min: 0,
        max: 140,
        relativeGaugeSize: true,
        title: "Aktuelle Geschwindigkeit"
    });

    $("#city-selection").change(function() {
        var newCity = $("#city-selection option:selected").text();
        $.ajax({
            url: URLremoteLocation,
            method: "PUT",
            data: newCity,
            success: function(data) {}
        });
    });

    initializeValues(gauge);
    initializeSubscriptions(gauge);
}


initializeWidgets();

</script>
</body>
</html>
```

- Um uns die Arbeit zu erleichtern, benutzen wir einige Frameworks:
 - Jquery (https://jquery.com/) und JQuery UI (https://jqueryui.com/) für Widgets
 - Bootstrap (http://getbootstrap.com/) zur Gestaltung des Layouts
 - JustGage (http://justgage.com/) mit Raphael (http://raphaeljs.com/) für die Darstellung der Geschwindigkeit
   - Zur korrekten Darstellung des Widgets müssen wir die aktuelle Version von JustGage aus dem GitHub-Repository herunterladen (https://github.com/toorshia/justgage/blob/master/justgage.js) und in dem Ordner, in dem sich auch unsere HTML-Datei befindet, als `justgage.js` abspeichern
- Um Rahmen um die einzelnen Widgets zu ziehen und die Hintergrundfarben anzupassen, benötigen wir außerdem die Datei `style.css` in unserem Ordner:
```css
.box {
	margin-top:10px;
	border: #cdcdcd medium solid;
	border-radius: 10px;
}

body {
	background-color: #8C8C8C;
}

.box {
	background-color: #DADADA;
}
```

- Die HTML-Datei definiert vier Widgets:
 - Geschwindigkeitsanzeige für die aktuelle Geschwindigkeit des Autos
 - Temperaturanzeige für einen wählbaren Ort
 - Zahleneingabefeld („Spinner“) für die die Stufenregelung des Innenraumlüfters
 - Dropdown-Menü für die Auswahl des Ortes, dessen Temperatur angezeigt wird (Auswahl zwischen Garching, Berlin und Bonn)
- Im `<script>`-Tag im zweiten Teil der Datei befindet sich die eigentliche Logik der Oberfläche
- Zunächst speichern wir uns die URLs der relevanten Parameter in Variablen ab – in diesem Fall gehen wir davon aus, dass der Server auf unserem lokalen Rechner läuft
- Anschließend definieren wir die Oberflächenlogik in einzelnen Funktionen, die zum Schluss der Reihe nach aufgerufen werden
- Als Erstes initialisieren wir in `initializeWidgets()` unsere Widgets und definieren, wie sie auf Änderungen durch den Benutzer reagieren
 - Beim Spinner (für den Innenraumlüfter) wird bei jeder Wertänderung der neue Wert über einen PUT-Request an die REST-API geschickt
 - Beim Dropdown (für die Ortswahl) wird bei jeder Änderung der Auswahl ebenfalls ein PUT-Request an die entsprechende URL gesendet
- Danach befüllen wir in `initializeValues()` unsere Widgets initial mit Werten, hierfür wird via AJAX für jeden Wert ein GET-Request an die passende Parameter-URL der REST-API geschickt
- Anschließend abonniert die GUI in `initializeSubscriptions()` Änderungen der Werte die vom Server geschickt werden
 - Jeder Parameter hat eine eindeutige URL, über die der aktuelle Wert abgefragt (GET-Request) sowie ein neuer Wert gesetzt (PUT-Request) werden kann
 - Durch Anhängen des Suffix `/subscription` können sich Clients außerdem für Push-Nachrichten vom Server registrieren („Server-Sent-Events“), diese werden gesendet sobald sich serverseitig ein Wert geändert hat
 - Hierfür wird die JavaScript-eigene Bibliothek „EventSource“ verwendet
 - Sobald der Server ein Update sendet, wird die Funktion aufgerufen die im Attribut onmessage gespeichert wird, in dieser Funktion wiederum wird das passende Widget aktualisiert
- Am Ende kann durch Öffnen der `index.html` im Browser das Dashboard angezeigt werden. 
