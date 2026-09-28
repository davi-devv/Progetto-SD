package it.unimib.sd2025;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Database implements IDatabase {
    private static Database instance;
    private final ConcurrentHashMap<String,String> data = new ConcurrentHashMap<>();

    private Database() {
        initializeDatabase();
    };

    // Metodo statico per ottenere l'istanza unica
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {  // Doppio controllo per garantire la singola istanza
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public String [] get(String ID, String... args) throws IOException {
        String[] keys = new String[args.length];
        String[] values = new String[args.length]; 

        for (int i = 0; i < args.length; i++) {
            keys[i] = ID + args[i]; 
            String value = data.get(keys[i]);  // Ottieni il valore dalla mappa
            if (value != null) {
                values[i] = value;
            } else {
                values[i] = null;  // Se la chiave non esiste
            }
        }
        return values;
    } 

    public boolean set(String ID, String... args) throws IllegalArgumentException {
        boolean result = true;
        for (String arg : args) {
            String[] parts = arg.split(String.valueOf(DatabaseReplier.KV_SEPARATOR));
            if (parts.length != 2) {
                throw new IllegalArgumentException("Formato non valido: " + arg);
            }
        }
        for (String arg : args) {
            String[] parts = arg.split(String.valueOf(DatabaseReplier.KV_SEPARATOR));
            String key = ID + parts[0]; // Chiave
            if (data.containsKey(key)) {
                String value = parts[1];  // Valore associato alla chiave
                data.put(key, value);
            } else {
                result = false; // La chiave non esiste, quindi l'operazione non è riuscita
            }
        }
        return result; 
    }

    public boolean delete(String ID, String ... keys) throws IOException {
            boolean result = true; // Variabile per tenere traccia del successo dell'operazione    
            for (int i = 0; i < keys.length; i++) {
                String key= ID + keys[i]; // Costruzione della chiave completa
                if (data.containsKey(key)) {
                    data.remove(key); // Rimuove la chiave dalla mappa
                } else {
                    result = false; // Imposta il risultato a false se una chiave non esiste
                }
            }
            return result;
        }

    public void add(String ID, String... args) throws IOException{
        if (args.length == 0) {
            throw new IOException("Nessun parametro fornito per costruire la chiave.");
        }

        for (String arg : args) {
            String[] parts = arg.split(String.valueOf(DatabaseReplier.KV_SEPARATOR));
            if (parts.length != 2) {
                throw new IOException("Formato non valido per l'argomento: " + arg);
            }
            String key = ID + parts[0]; // Chiave
            String value = parts[1];  // Valore associato alla chiave
            data.put(key, value);
        } 
    }

    public boolean increment(String ID, String... args) throws IOException {
        boolean result = true;
        if (args.length == 0) {
            throw new IOException("Nessun parametro fornito per costruire la chiave.");
        }
        for (String arg : args) {
            String[] parts = arg.split(String.valueOf(DatabaseReplier.KV_SEPARATOR));
            if (parts.length != 2) {
                throw new IOException("Formato non valido per l'argomento: " + arg);
            }
            String key = ID + parts[0]; // Chiave
            System.out.print("Cerco chiave "+key);
            if (data.containsKey(key)) {
                String value = parts[1];
                System.out.println(" ok");
                data.compute(key, (k, oldVal) -> {
                    if (oldVal == null) return null;  // opzionale, nel tuo caso probabilmente mai null qui

                    if (value.contains(".") || oldVal.contains(".")) {
                        float newValue = Float.parseFloat(value);
                        float oldValue = Float.parseFloat(oldVal);
                        return String.valueOf(oldValue + newValue);
                    } else {
                        int newValue = Integer.parseInt(value);
                        int oldValue = Integer.parseInt(oldVal);
                        return String.valueOf(oldValue + newValue);
                    }
                });

            } else {
                System.out.println(" non trovata.");
                result = false; // La chiave non esiste, quindi l'operazione non è riuscita
                break;
            }
        }
        return result; 
    }
    

    private void initializeDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/it/unimib/sd2025/resources/data.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(String.valueOf(DatabaseReplier.KV_SEPARATOR));
                if (parts.length == 2) {
                    data.put(parts[0], parts[1]); 
                } else {
                    System.err.println("Formato invalido: " + line);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File non Trovato: " + e.getMessage());
        } 
        catch (IOException e) {
            System.err.println("Errore lettura file: " + e.getMessage());
        }
        System.out.println("Database inizializzato con successo.");
    }
}
