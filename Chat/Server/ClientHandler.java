import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    private Chatters clients;
    private TapeRecorder audioPlayer; 

    public ClientHandler(Socket socket, Chatters clients) {
        this.clients = clients;
        this.clientSocket = socket;
        this.audioPlayer = new TapeRecorder();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menu(){
        out.println("\n/creategroup <nombre_grupo>: Crea un nuevo grupo (el usuario que crea el grupo no se agrega automáticamente al mismo).\n" +
                "/join <nombre_grupo>: Une al usuario al grupo especificado.\n" +
                "/leave <nombre_grupo>: Elimina al usuario del grupo especificado.\n" +
                "/send #<nombre_grupo>|<mensaje>: Envía un mensaje al grupo especificado.\n" +
                "/send @<usuario>|<mensaje>: Envía un mensaje a un usuario específico.\n" +
                "/audio #<nombre_grupo>: Envía un mensaje de voz de 15 segundos al grupo especificado.\n" +
                "/audio @<usuario>: Envía un mensaje de voz de 15 segundos al usuario especificado.\n");
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
                        menu();
                        break;
                    }
                }
            }


            String message;
            while ((message = in.readLine()) != null) {

                if (message.startsWith("/creategroup")) {
                    String groupName = message.substring(13);
                    createGroup(groupName);
                    menu();
                } else if (message.startsWith("/join")) {
                    String groupName = message.substring(6);
                    joinGroup(groupName);
                    menu();
                } else if (message.startsWith("/leave")) {
                    String groupName = message.substring(7);
                    leaveGroup(groupName);
                    menu();
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
                    menu();
                }else if (message.startsWith("/audio")) {
                    String[] parts = message.split(" ", 2);
                    String target = parts[1];
                    if (target.startsWith("#")) {
                        String groupName = target.substring(1);
                        clients.sendVoiceMessageToGroup(groupName, clientName);
                    } else if (target.startsWith("@")) {
                        String receiverUser = target.substring(1);
                        clients.sendPrivateVoiceMessage(clientName, receiverUser);
                    }
                    menu();
                }else if (message.equals("/verhistorial")) {
                    showHistory(clientSocket, clients);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.println(" haz abandonado el chat, debes volver a ejecutar un Client.java si quieres volver a unirte");
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

    private void showHistory(Socket socket, Chatters clients) throws IOException {
        out= new PrintWriter(socket.getOutputStream(), true);
        StringBuilder history = clients.gethistory();
        out.println("Historial completo:");
        out.println(history.toString());
    }
}
