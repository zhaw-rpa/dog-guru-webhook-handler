Björn Scheppler, 30.4.2021

# Dog Guru Webhook Handler für Azure Spring Cloud
TBD...
Dieses Maven-Projekt kann genutzt werden als Startpunkt für eigene auf Spring Boot, JPA und REST beruhende Projekte. Enthalten sind folgende Funktionalitäten:
1. Spring Boot
2. Spring Boot Starter Web für Tomcat sowie REST-Komponenten
4. H2-Datenbank-Unterstützung
5. Spring Boot Starter JPA für Datenbank-Zugriff
6. "Sinnvolle" Grundkonfiguration in application.properties für Datenbank, REST und Tomcat
7. Daten-/REST-Komponenten:
    1. DemoEntity
    2. DemoRepository
    3. data.sql

## Grundlegende Nutzung
1. http://localhost:8070 aufrufen
2. Error-Page wird angezeigt, sofern Server erfolgreich gestartet wurde
3. http://localhost:8070/api/ eingeben
4. Einstiegspunkt zu API wird angezeigt

## Fortgeschrittene Nutzung (H2 Console)
1. Um auf die Datenbankverwaltungs-Umgebung zuzugreifen, http://localhost:8070/console eingeben.
2. Anmeldung über:
    1. Benutzername sa
    2. Passwort: leer lassen
    3. URL jdbc:h2:./restDb