    import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Chatters {
    private Set<Person> clients = new HashSet<>();
    private Map<String, Group> groups = new HashMap<>();
    private StringBuilder history = new StringBuilder();

    public Chatters() {}

    public boolean existeUsr(String name) {
        boolean response = false;
        for (Person p : clients) {
            if (name.equals(p.getName())) {
                response = true;
                break;
            }
        }
        return response;
    }
    public boolean existsGroup(String groupName) {
        return groups.containsKey(groupName);
    }

    public void addUsr(String name, PrintWriter out) {
        if (!name.trim().isEmpty() && !existeUsr(name)) {
            Person p = new Person(name, out);
            clients.add(p);
        }
    }

    public void removeUsr(String name) {
        for (Person p : clients) {
            if (name.equals(p.getName())) {
                clients.remove(p);
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Person p : clients) {
            p.getOut().println(message);
        }
        history.append(message).append("\n");
    }

    public void sendPrivateMessage(String nameSrc, String nameDest, String message) {
        boolean encontrado = false;
        for (Person p : clients) {
            if (nameDest.equalsIgnoreCase(p.getName())){
                p.getOut().println("[Chat privado de " + nameSrc + "]: " + message);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            history.append("[Chat privado de " + nameSrc + " para " + nameDest + "]: " + message).append("\n");
            try {
                saveHistory(history);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontró al usuario " + nameDest);
        }
    }

    public void createGroup(String groupName) {
        if (!groups.containsKey(groupName)) {
            Group group = new Group(groupName);
            groups.put(groupName, group);
        }
    }

    public void joinGroup(String groupName, String clientName, PrintWriter out) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.addMiembro(out);
            String mensaje = clientName + " se ha unido al grupo " + groupName + ".";
            notifyGroup(groupName, mensaje);
        }
    }

    private void notifyGroup(String groupName, String mensaje) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.enviarMensaje(mensaje);
        }
    }

    public void leaveGroup(String groupName, String clientName, PrintWriter out) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.removeMiembro(out);
            broadcastMessage(clientName + " ha salido del grupo " + groupName + ".");
        }
    }

    public void sendMessageGroup(String groupName, String mensaje) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.enviarMensaje(mensaje);
            history.append("[Grupo " + groupName + "] " + mensaje).append("\n");
            try {
                saveHistory(history);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    public Map<String, Group> getGroups() {
        return groups;
    }
    
    public void sendVoiceMessageToGroup(String groupName, String senderName) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        for (Person p : clients) {
            if (senderName.equals(p.getName())) {
                p.getOut().println("Grabando...");
                byteArrayOutputStream = p.getAudioRecorder().recordAudio();
                p.getOut().println("Grabación terminada");
            }
        }
    
        if (existsGroup(groupName)) {
            Group group = groups.get(groupName);
            for (PrintWriter escritor : group.getMembers()) {
                for (Person p : clients) {
                    if (escritor.equals(p.getOut()) && !p.getName().equals(senderName)) {
                        p.getOut().println("[Group: " + groupName + ", Sender: " + senderName + "] Audio:");
                        p.getOut().println("Reproduciendo");
                        p.getAudioRecorder().reproduceAudio(byteArrayOutputStream);
                    }
                }
            }
            history.append("[Grupo " + groupName + "] " + senderName + " envió un audio").append("\n");
            try {
                saveHistory(history);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPrivateVoiceMessage(String senderName, String recipientName){
        ByteArrayOutputStream byteArrayOutputStream = null;
        for (Person p: clients) {
            if (senderName == p.getName()){
                p.getOut().println("Grabando...");
                byteArrayOutputStream = p.getAudioRecorder().recordAudio();
                p.getOut().println("Grabacion terminada");
            }
        }
        for (Person p : clients) {
            if (recipientName.equals(p.getName())) {
                p.getOut().println("[Private audio from " + senderName + "] ");
                p.getOut().println("Reproduciendo");
                p.getAudioRecorder().reproduceAudio(byteArrayOutputStream);
            }
        }
        history.append("[Chat privado] " + senderName + " envió un audio a " + recipientName).append("\n");
        try {
            saveHistory(history);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String folder = "Chat/Historial";
    static String path = "Chat/Historial/history.txt";
    

    public static void saveHistory(StringBuilder history) throws IOException {
        File file = new File(path);
            if (!file.exists()) {
                File f = new File(folder);
                if (!f.exists()) {
                    f.mkdirs();
                }
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file, true); 
            writer.write(history.toString());
            writer.flush();
            writer.close();
        }

    public StringBuilder gethistory() {
        return history;
    }
}


