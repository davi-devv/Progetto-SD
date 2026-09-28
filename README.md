# Progetto Sistemi Distribuiti 2024-2025 - CultuVAD

## Descrizione

Questo progetto è stato sviluppato nell’ambito del corso di Sistemi Distribuiti (a.a. 2024-2025) e ha come obiettivo la realizzazione di un’applicazione distribuita per la gestione della Carta Cultura Giovani, un'iniziativa che assegna a ogni utente un contributo iniziale di 500€ da utilizzare per l'acquisto di beni e servizi culturali.
L'architettura del sistema e suddivisa in tre componenti principali:

### Client Web

Il Client Web (HTML + JavaScript) è interfaccia utente che consente di visualizzare lo stato del contributo, generare, modificare o consumare buoni, e registrare nuovi utenti. Il client comunica con il server tramite [API_REST](./REST.md).

### Server

Il Server Web (Java con JAX-RS e JSON-B) gestisce la logica dell'applicazione, l'accesso concorrente alle risorse e funge da intermediario tra il client e il database. Comunica con il database tramite un protocollo personalizzato su socket [TCP](./TCP.md).

### Database

La componente Database (Java) implementa un semplice database in-memory chiave-valore, ispirato a Redis, per la memorizzazione dei dati relativi a utenti, contributi e buoni. Supporta più connessioni simultanee e gestisce la concorrenza in lettura/scrittura.
La struttura del database è particolare: le chiavi sono costruite concatenando un identificativo (che possiamo interpretare come una primary key SQL) ai nomi di dominio specificati nei parametri.

## Componenti del gruppo

* Vittoria Mian
* Angelo Mario Boni
* Davide Molteni

## Compilazione ed esecuzione

Sia il server Web sia il database sono applicazioni Java gestite con Maven. All'interno delle rispettive cartelle si può trovare il file `pom.xml` in cui è presenta la configurazione di Maven per il progetto. Si presuppone l'utilizzo della macchina virtuale di laboratorio, per cui nel `pom.xml` è specificato l'uso di Java 21.

Il server Web e il database sono dei progetti Java che utilizano Maven per gestire le dipendenze, la compilazione e l'esecuzione.

### Client Web

Per avviare il client Web è necessario utilizzare l'estensione "Live Preview" su Visual Studio Code, come mostrato durante il laboratorio. Tale estensione espone un server locale con i file contenuti nella cartella `client-web`.

### Server Web

Il server Web utilizza Jetty e Jersey. Si può avviare eseguendo `mvn jetty:run` all'interno della cartella `server-web`. Espone le API REST all'indirizzo `localhost` alla porta `8080`.

### Database

Il database è una semplice applicazione Java. Si possono utilizzare i seguenti comandi Maven:

* `mvn clean`: per ripulire la cartella dai file temporanei,
* `mvn compile`: per compilare l'applicazione,
* `mvn exec:java`: per avviare l'applicazione (presuppone che la classe principale sia `Main.java`). Si pone in ascolto all'indirizzo `localhost` alla porta `3890`.

### Utenti già presenti nel database all'avvio (In caso di testing)

E' possibile, sin da subito, loggarsi con questi codici fiscali per testare le funzionalità del progetto:
- RSSMRA85T10A562S --> Utente con 1 buono consumato da 300.00€ e 1 buono non consumato da 100.00€
- VRDLGI85A01F205Z --> Utente con 1 buono consumato da 105.50€
- PLLRSS89E12C351B --> Utente con 0 buoni
- BNCLRA76C45D612X --> Utente con 0 buoni
- FRNLCZ00Q22H123Y --> Utente con 0 buoni
