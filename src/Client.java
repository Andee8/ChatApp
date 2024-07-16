import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done = false;

    @Override
    public void run() {
        try {
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread inputThread = new Thread(inputHandler);
            inputThread.start();

            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null && !done) {
                System.out.println(receivedMessage);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            System.err.println("Error while shutting down the client: " + e.getMessage());
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                while (!done && (userInput = stdIn.readLine()) != null) {
                    out.println(userInput);
                    if (userInput.equals("/quit")) {
                        shutdown();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error in input handler: " + e.getMessage());
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        new Thread(client).start();
    }
}
