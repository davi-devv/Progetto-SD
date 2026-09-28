# Progetto Sistemi Distribuiti 2024-2025 - TCP

***Documentazione del protocollo su socket TCP che espone il database.***


## 1. Panoramica

- **Tipo:** testuale 
- **Porta Utilizzata:** 3890

#### 1.1 Descrizione:

Il protocollo sfrutta il metodo di accorpamento ID-chiave del database

Crea un singolo messaggio che abbia costantemente un valore identificativo
dopo il comando. Dopo di esso, vengono specificate le chiavi (e i rispettivi valori,
se l'operazione lo richiede).  
La risposta ritorna sempre un codice e tutti i valori richiesti associati in risposta,
sempre separati dal delimitatore.


## 2. Struttura dei Messaggi

- **Encoding:** UTF-8
- **Fine linea:** LF (Unix) & CRLF (Windows)
- **Delimitatori Messaggio:** newline
- **Delimitatori Variabili:** "**;**"
- **Divisore chiave-valore:** "**:**"

**Esempio:**
```
GETCMD;{ID};{key1};{key2}
SETCMD;{ID};{key1}:{val1};{key2}:{val2}
```

#### Comandi

| Comando | Parametri           | Descrizione                                                                  | Esempio                  |
|---------|---------------------|------------------------------------------------------------------------------|--------------------------|
| SET     | id, chiavi:valori   | Modifica a ogni chiave, il rispettivo valore                                 | `SET;{ID};chiave1:Ciao`  |
| GET     | id, chiavi          | Restituice i valori associato alle chiavi                                    | `GET;{ID};chiave1`       |
| ADD     | id, chiavi:valori   | Associa forzatamente a ogni chiave il rispettivo valore                      | `ADD;{ID};chiave1:42`    |
| DEL     | id, chiavi          | Cancella ogni chiave specificata                                             | `DEL;{ID};chiave1`       |
| INCR    | id, chiavi:quantità | Aumenta ogni chiave della quantità specificata                               | `INCR;{ID};chiave1:-2.5` |
| QUIT    | AUTH::KEY           | Chiude la connessione solo se il parametro d'identificazione è \"AUTH::KEY\" | `QUIT;AUTH::KEY`         |

---
#### Risposte
| Comando   | Descrizione                                                                          | Esempio                           |
|-----------|--------------------------------------------------------------------------------------|-----------------------------------|
| OK        | L'operazione è andata a buon fine. Può ritornare dei parametri opzionali             | `OK;Mario;Rossi`                  |
| NOT_FOUND | Non è stata trovata una chiave inserita per quell'ID                                 | `NOT_FOUND`                       |
| ERROR     | C'è stato un errore generico con il database. Può ritornare il messaggio di errore   | `Error;For input string: "abc"`   |

## 3. Gestione degli Errori
- Risposte di errore (es. `{codice};{descrizione}`)
- Esempi di messaggi di errore:
  - `ERROR;can't use function Integer.parseInt on 25.0`
  - `NOT_FOUND`

## 4. Scambio di Esempio
```
Raw message: GET;System;Utenti;Free;Held;Sold;NBN;NBC
Raw response: OK;5;2500.0;0.0;0.0;0;0

Raw message: GET;VRDLGI85A01F205Z;nome;cognome;email
Raw response: OK;Luigi;Verdi;luigi.verdi@example.com

Raw message: INCR;VRDLGI85A01F205Z;last:1;saldo:-123.0;held:123.0
Raw response: OK;
```