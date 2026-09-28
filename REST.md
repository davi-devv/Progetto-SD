# Progetto Sistemi Distribuiti 2024-2025 - API REST

L'API RESTful costituisce l'interfaccia di comunicazione tra il client e il server.   
Tutte le richieste e risposte utilizzano il formato JSON, con intestazioni HTTP appropriate. Gli endpoint sono
progettati per garantire semplicità d'uso, supportando i metodi HTTP standard (GET, POST, PUT).   
L'accesso ad alcuni endpoint richiede autenticazione tramite instestazione Authentication-ID.

## /login/{codiceFiscale}

### GET

Chiamata in [login.js](../skeleton/client-web/js/login.js)   
Tutte le call al server vengono svolte da [handleServerRequest.js](../skeleton/client-web/js/handlers/handleServerRequest.js).
Non verrà più specificato in seguito.

Server riceve in [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/login/LoginHandler.java)

- **Descrizione**: Restituisce le informazioni dell'utente con codice fiscale pari a {codiceFiscale}.
    L'endpoint analizza il PathParameter {codiceFiscale} e invia una richiesta al database, che restituisce i dati dell'utente (nome, cognome e email). 
    I dati vengono quindi parsati e restituiti al client sotto forma di JSON.
- **Parametri**: `codiceFiscale`
- **Header**: `Content-Type: application/json`
- **Body Richiesta**: Nessuno.
- **Risposta**: Un oggetto JSON: `{ "name": name, "surname": surname, "email": email }`
- **Codici di stato restituiti**: 
    - `200 OK`: Dati restituiti con successo, richiesta andata a buon fine.
    - `404 Not Found`: L'utente non è presente nel database.
    - `500 Internal Server Error`: Errore generico interno del server (previsto per ogni chiamata).



## /balance

Chiamata Client: [balance.js](../skeleton/client-web/js/balance.js)

Server riceve in: [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a: [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/balance/BalanceHandler.java)

### GET
- **Descrizione**: Restituisce il bilancio dell'utente. 
    Il codice fiscale {CF} viene specificato negli header della richiesta, quindi non è necessario 
    l'utilizzo di alcun Path Parameter o Request Body. Il server invia una richiesta al database, che 
    restituisce il bilancio dell'utente.   
    I dati vengono quindi parsati e restituiti al client in formato JSON.
- **Parametri**: Nessuno.
- **Header**: 
    - `Content-Type: application/json`
    - `Authentication-ID: CF`
- **Body Richiesta**: Nessuno.
- **Risposta**: Un oggetto JSON: 
```
{ 
        "freeBalance": freeBalance,
        "usedNotConsumed": usedNotConsumed  
}
```
- **Codici di stato restituiti**: 
    - `200 OK`: Dati restituiti con successo, richiesta andata a buon fine.
    - `401 UNAUTHORIZED`: Nel caso in cui il {CF} nell'header della richiesta sia non valido o non specificato. 
        E' necessario essere loggati per utilizzare questo endpoint.
    - `404 Not Found`: Il bilancio dell'utente non è presente nel database. 
        In teoria, questo codice non dovrebbe mai essere restituito, poiché il bilancio viene creato 
        alla registrazione dell’utente con un valore iniziale di 500.
    - `500 Internal Server Error`: Errore generico interno del server (previsto per ogni chiamata).

Variabili utilizzate:
`freeBalance`: Bilancio libero dell'utente, il quale può essere utilizzato per creare nuovi buoni.
`usedNotConsumed`: Somma dei valori di tutti i buoni appartenenti all'utente **non** ancora consumati.
`usedAndConsumed`: Somma dei valori di tutti i buoni appartenenti all'utente e utilizzati. E' ricavabile lato client facendo 500 - 
`freeBalance` - `usedNotConsumed`.   
Si utilizzano queste tre variabili in quanto, come da specifica, il bilancio dell'utente deve riportare:
- La quantità di contributo disponibile per generare dei buoni;
- La quantità di contributo usata per generare dei buoni che non sono stati ancora utilizzati;
- La quantità di contributo usata per genereare dei buoni che sono stati consumati.



## /register

Chiamata Client: [register.js](../skeleton/client-web/js/register.js)

Server riceve in: [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a: [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/register/RegisterHandler.java)

### POST
- **Descrizione**: Registra un nuovo utente nel sistema. 
    Il Codice Fiscale {CF} non viene specificato nell'header, poiché l'utente non è ancora loggato. 
    Il codice fiscale viene specificato nel Request Body.
    Il server verifica se il codice fiscale è già presente. In tal caso, restituisce un codice 409 CONFLICT; 
    altrimenti, inserisce i dati nel database, assegna un bilancio iniziale di 500€ e aggiorna le variabili globali.
- **Parametri**: Nessuno.
- **Header**: 
    - `Content-Type: application/json`
- **Body Richiesta**: Un oggetto JSON: 
```
    {
        "CF": CF
        "nome": nome,
        "cognome": cognome,
        "email": email
    }
```
- **Risposta**: Nessuno.
- **Codici di stato restituiti**: 
    - `200 OK`: Richiesta andata a buon fine, dati creati correttamente nel database.
    - `400 Bad Request`: La struttura del Request Body è errata.
    - `409 CONFLICT`: L'utente con questo Codice Fiscale già esiste nel database.
    - `500 Internal Server Error`: Errore interno del server (previsto per ogni chiamata).



## /stats

Chiamata Client: [stats.js](../skeleton/client-web/js/stats.js)

Server riceve in: [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a: [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/stats/StatsHandler.java)

### GET
- **Descrizione**: Restituisce le variabili globali che descrivono lo stato del sistema. 
    L'accesso è pubblico e non richiede autenticazione.
    Il server interroga il database e restituisce le sei variabili sotto forma di JSON.
- **Parametri**: Nessuno.
- **Header**: `Content-Type: application/json`
- **Body Richiesta**: Nessuno.
- **Risposta**: Un oggetto JSON: 
```
{
    "totalUsers": totalUsers,
    "contributiDisponibili": contributiDisponibili, 
    "nonAncoraSpesi": nonAncoraSpesi,
    "spesi": spesi,
    "buoniGeneratiConsumati": buoniGeneratiConsumati,
    "buoniGeneratiNonConsumati": buoniGeneratiNonConsumati
}
```
- **Codici di stato restituiti**:
    - `200 OK`: Dati restituiti con successo, richiesta andata a buon fine.
    - `500 Internal Server Error`: Errore interno del server (previsto per ogni chiamata).

Si utilizzano le seguenti variabili:   
- `totalUsers`: Numero di utenti registrati all'interno del sistema;
- `contributiDisponibili`: Somma di tutti i bilanci degli utenti;
- `nonAncoraSpesi`: Somma dei valori di tutti i buoni **NON** ancora spesi;
- `spesi`: Somma dei valori di tutti i buoni **SPESI**;
- `buoniGeneratiConsumati`: Il numero di buoni generati **SPESI**;
- `buoniGeneratiNonConsumati`: Il numero di buoni generati **NON** spesi.



## /history

Chiamata Client: [stats.js](../skeleton/client-web/js/history.js)

Server riceve in: [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a: [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/history/HistoryHandler.java)

# GET
- **Descrizione**: Restituisce la cronologia dei buoni creati dall'utente. E' una funzione di estrema importanza, in quanto nel sistema
    non è prevista una chiamata per ritornare un singolo buono, bensì si è preferito ritornare una lista con tutti i buoni appartenenti
    all'utente, così da rendere la loro visualizzazione tabellare più semplice;   
    In nessun caso una chiamata di tipo `/get/{buonoId}` (per fare un esempio) sarebbe stata utile all'utente finale senza la 
    visualizzazione a tabella implementata ora, di conseguenza, si è preferito ometterla a favore di una chiamata che restituisse tutti i buoni
    creati dall'utente.   
    L'utilizzo dell'endpoint richiede che l'utente sia loggato e che il Codice Fiscale {CF} venga inserito all'interno dell'Header della
    richiesta.   
    Il server restituisce una mappa JSON con gli ID dei buoni come chiavi e i relativi dettagli come valori.
- **Parametri**: Nessuno.
- **Header**: 
    - `Content-Type: application/json`
    - `Authentication-ID: CF`
- **Body Richiesta**: Nessuno.
- **Risposta**: Una mappa JSON nella forma
    ```
        {
            listaBuoni: {
                "1": {
                    "id": 1,
                    "value": 100,
                    "type": 2,
                    "consumed": true,
                    "created": DATA,
                    "modified": DATA 
                },
                "2": ...
            }
        }
    ```
- **Codici di stato restituiti**:
    - `200 OK`: Dati restituiti con successo, richiesta andata a buon fine.
    - `401 Unauthorized`: Nel caso in cui il {CF} nell'header della richiesta sia non valido o non specificato. 
        E' necessario essere loggati per utilizzare questo endpoint.
    - `500 Internal Server Error`: Errore interno del server (previsto per ogni chiamata)

Variabili utilizzate nei buoni:
- `id`: L'identificativo del buono; Esso non è esclusivo con tutti gli altri buoni generati da altri utenti, in quanto la reale chiave 
    nel database per un buono è legata al CF dell'utente, come descritto in [TCP.md](./TCP.md);
- `value`: Valore del buono in euro;
- `type`: Il tipo di buono. E' un intero che va da 0 a 8:
    - `0`: Cinema
    - `1`: Musica
    - `2`: Concerti
    - `3`: Eventi culturali
    - `4`: Libri
    - `5`: Musei
    - `6`: Strumenti musicali
    - `7`: Teatro
    - `8`: Danza
- `consumed`: `true` o `false`, indica se il buono in questione è stato consumato o meno;
- `created`: Data che indica se quando il buono è stato creato;
- `modified`: Data che indica l'ultima volta che un buono è stato modificato.



## /create

Chiamata Client: [stats.js](../skeleton/client-web/js/create.js)

Server riceve in: [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a: [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/create/CreateHandler.java)

# POST
- **Descrizione**: Crea un nuovo buono. L'utente deve essere loggato per poter creare un nuovo buono.   
    Il Codice Fiscale {CF} deve essere specificato negli header della richiesta, come in tutte le richieste
    in cui si necessita che l'utente si sia loggato in precedenza.   
    Il Request Body deve contenere un oggetto JSON con valore e tipo del buono.   
    Il server crea il buono, lo inserisce nel database con le date correnti e restituisce lo stato dell’operazione.
- **Parametri**: Nessuno.
- **Header**: 
    - `Content-Type: application/json`
    - `Authentication-ID: CF`
- **Body Richiesta**: Un oggetto JSON: `{ "value": value, "type": type }`
- **Risposta**: Nessuno.
- **Codici di stato restituiti**:
    - `200 OK`: Richiesta avvenuta con successo, buono creato e inserito nel database.
    - `401 Unauthorized`: Nel caso in cui il {CF} nell'header della richiesta sia non valido o non specificato. 
        E' necessario essere loggati per utilizzare questo endpoint.
    - `403 Forbidden`: Bilancio insufficiente. Il controllo viene normalmente effettuato lato client; 
        è possibile forzare la richiesta con strumenti come CURL.
    - `500 Internal Server Error`: Errore interno del server (previsto per ogni chiamata).



## /modify/{idBuono}

Chiamata Client: [stats.js](../skeleton/client-web/js/modify.js)

Server riceve in: [APIResource.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/APIResource.java)   
E passa il comando a: [LoginHandler.java](../skeleton/server-web/src/main/java/it/unimib/sd2025/handler/modify/ModifyHandler.java)

# PUT
- **Descrizione**: Modifica un buono esistente.   
    Il server verifica se il bilancio è sufficiente e se il buono non è stato già consumato. Se entrambi i controlli sono superati,
    il server procede con l'operazione di modifica, aggiornando anche il bilancio dell'utente e le variabili generali.   
    Ritorna infine il codice sullo status della richiesta.
- **Parametri**: `idBuono`
- **Header**: 
    - `Content-Type: application/json`
    - `Authentication-ID: CF`
- **Body Richiesta**: Un oggetto JSON: 
```
{ 
    `type`: type,
    `value`: value,
    `consumed`: consumed
}
```
- **Risposta**: Nessuno.
- **Codici di stato restituiti**:
    - `200 OK`: Richiesta avvenuta con successo, database modificato con i nuovi valori.
    - `401 Unauthorized`: Nel caso in cui il {CF} nell'header della richiesta sia non valido o non specificato. 
        E' necessario essere loggati per utilizzare questo endpoint.
    - `404 Not Found`: L'idBuono specificato non esiste. Come nel caso precedente, non è possibile ottenere questo codice
        con le funzionalità del client standard.
    - `500 Internal Server Error`: Errore interno del server (previsto per ogni chiamata).