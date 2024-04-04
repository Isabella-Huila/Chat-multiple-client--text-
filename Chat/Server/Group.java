import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class Group {
    private String name;
    private Set<PrintWriter> members;

    public Group(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void addMiembro(PrintWriter escritor) {
        members.add(escritor);
    }

    public void removeMiembro(PrintWriter escritor) {
        members.remove(escritor);
    }

    public void enviarMensaje(String mensaje) {
        for (PrintWriter escritor : members) {
            escritor.println(name + ": " + mensaje);
        }
    }
}
