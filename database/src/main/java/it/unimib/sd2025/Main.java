package it.unimib.sd2025;

import java.net.*;
import java.io.*;

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = DatabaseReplier.DATABASE_PORT;
    private static final IDatabase db = Database.getInstance(); // Unico database condiviso

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     */
    public static void startServer() throws IOException {
        var server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);

        try {
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }

    /**
     * Handler di una connessione del client.
     */
    private static class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        public void run() {
            System.out.println("Accepted new client "+client.getPort());

            // Try-with-resources chiude automaticamente tutto in caso di errore
            try (
                    var out = new PrintWriter(client.getOutputStream(), true);
                    var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                ) {
                String inputLine;
                var repl = new DatabaseReplier(db);
                while ((inputLine = in.readLine()) != null) {
                    var res = repl.reply(inputLine);
                    out.println(res.formatMessage());
                    if (res.getCode() == DatabaseReplier.DatabaseResponse.ResponseOperation.QUIT)
                        break;
                }

                in.close();
                out.close();
                client.close();
                System.out.println("Quitted user "+client.getPort());
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        startServer();
    }
}
