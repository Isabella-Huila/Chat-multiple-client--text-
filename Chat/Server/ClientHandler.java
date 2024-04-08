import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    private Chatters clients;
    public ClientHandler(Socket socket, Chatters clients) {
        this.clients = clients;
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                out.println("SUBMITNAME");
                clientName = in.readLine();
                if (clientName == null) {
                    return;
                }
                synchronized (clientName) {
                    if (!clientName.trim().isEmpty() && !clients.existeUsr(clientName)) {
                        clients.broadcastMessage(clientName + " se ha unido al chat.");
                        out.println("NAMEACCEPTED " + clientName);
                        clients.addUsr(clientName, out);
                        break;
                    }
                }
            }
            /**
             * /creategroup <nombre_grupo>: Crea un nuevo grupo.
             * /join <nombre_grupo>: Une al usuario al grupo especificado.
             * /leave <nombre_grupo>: Elimina al usuario del grupo especificado.
             * /send #<nombre_grupo>|<mensaje>: Envía un mensaje al grupo especificado.
             * /send @<usuario>|<mensaje>: Envía un mensaje a un usuario específico.
             */

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/creategroup")) {
                    String groupName = message.substring(13);
                    createGroup(groupName);
                } else if (message.startsWith("/join")) {
                    String groupName = message.substring(6);
                    joinGroup(groupName);
                } else if (message.startsWith("/leave")) {
                    String groupName = message.substring(7);
                    leaveGroup(groupName);
                } else if (message.startsWith("/send")) {
                    String[] parts = message.substring(6).split("\\|", 2);
                    String target = parts[0].trim();
                    String content = parts[1].trim();
                    if (target.startsWith("#")) {
                        String groupName = target.substring(1);
                        sendMessageGroup(groupName, content);
                    } else if(target.startsWith("@")){
                        clients.sendPrivateMessage(clientName, target.substring(1), content);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println(clientName + " ha abandonado el chat.");
                clients.broadcastMessage(clientName + " ha abandonado el chat.");
                clients.removeUsr(clientName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGroup(String groupName) {
        clients.createGroup(groupName);
    }

    private void joinGroup(String groupName) {
        clients.joinGroup(groupName, clientName, out);
    }

    private void leaveGroup(String groupName) {
        clients.leaveGroup(groupName, clientName, out);
    }

    private void sendMessageGroup(String groupName, String mensaje) {
        clients.sendMessageGroup(groupName, clientName + ": " + mensaje);
    }
}
