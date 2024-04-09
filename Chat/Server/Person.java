import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.io.PrintWriter;
import java.net.Socket;

//
public class Person {
    private String name;
    PrintWriter out;

    private TapeRecorder audioRecorder;

    public Person(String name, PrintWriter out){
        this.name = name;
        this.out  = out;
    }
   
    public String getName() {
        return name;
    }
    
    public PrintWriter getOut() {
        return out;
    }

    public TapeRecorder  getAudioRecorder(){
        audioRecorder =new TapeRecorder();
        return audioRecorder;
    }


    
}