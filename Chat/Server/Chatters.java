import java.util.*;
import java.io.PrintWriter;


public class Chatters {
    private Set<Person> clients = new HashSet<>();
    private Map<String, Group> groups = new HashMap<>();

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

    public void addUsr(String name, PrintWriter out) {
        if (!name.isBlank() && !existeUsr(name)) {
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
        if (!encontrado) {
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
        }
    }

    public Map<String, Group> getGroups() {
        return groups;
    }
}