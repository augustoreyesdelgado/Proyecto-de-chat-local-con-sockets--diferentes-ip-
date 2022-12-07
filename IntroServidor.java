package proyUalter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*

¿Qué sucede si se manda un mensaje a un cliente que no está disponible?
R.- Por lo realizado en la linas 55 a 60, los metodos chat (linea 70) y avisar (linea 9), solo salta una excepción y se avisa que no se pudo conectar.
¿Cómo se administra un mensaje que se mandó a un cliente no disponible y en un determinado momento se conecta, se le hace llegar el mensaje o no?
R.- El mensaje se pierde, no se envia al receptor cuando este se conecta.
 */

public class IntroServidor {
    public static void main(String[] args) {
        Servidor miserver = new Servidor();
    }
}

class Servidor implements Runnable{

    public Servidor(){
        Thread servidor1=new Thread(this);
        servidor1.start();
    }

    @Override
    public void run() {
        System.out.println("Iniciando servidor...");
        try {
            ServerSocket ss = new ServerSocket(7777);
            String nombre, mensaje, destino, ip;
            Paquete paquete_recibido;
            while(true) {
                Socket s = ss.accept();
                ObjectInputStream paquete_datos =new ObjectInputStream(s.getInputStream());
                paquete_recibido= (Paquete) paquete_datos.readObject();
                /*
                Socket s = ss.accept();
                DataInputStream dis = new DataInputStream(s.getInputStream());
                System.out.print(">> ");
                peek = (String) dis.readUTF();
                System.out.println(peek);
                chat(peek);
                s.close();*/
                ip=paquete_recibido.getIp();
                destino=paquete_recibido.getDestino();
                nombre=paquete_recibido.getNombre();
                mensaje=paquete_recibido.getMensaje();
                System.out.print("IP>> ");
                System.out.println(ip+"("+nombre+") para ("+destino+")"+mensaje);
                try {
                    chat(paquete_recibido);
                    System.out.println("Exito");
                }catch (Exception e){
                    System.out.println(e);
                    avisar(paquete_recibido);
                }
                s.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    static void chat(Paquete paquete_recibido) throws IOException {

        Socket s = new Socket(paquete_recibido.getDestino(), 7777);

        Paquete datos = new Paquete();

        datos.setIp(paquete_recibido.getIp());
        datos.setNombre(paquete_recibido.getNombre());
        datos.setMensaje(paquete_recibido.getMensaje());
        datos.setDestino(paquete_recibido.getDestino());

        ObjectOutputStream paquete_datos = new ObjectOutputStream(s.getOutputStream());
        paquete_datos.writeObject(datos);

        /*DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF(cad);*/
        paquete_datos.flush();
        paquete_datos.close();
        s.close();
    }
    static void avisar(Paquete paquete_recibido) throws IOException {

        Socket s = new Socket(paquete_recibido.getIp(), 7777);

        Paquete datos = new Paquete();

        datos.setIp(paquete_recibido.getIp());
        datos.setNombre("SERVIDOR");
        datos.setMensaje("No se pudo conectar con ("+paquete_recibido.getDestino()+") :'(");
        datos.setDestino(paquete_recibido.getDestino());

        ObjectOutputStream paquete_datos = new ObjectOutputStream(s.getOutputStream());
        paquete_datos.writeObject(datos);

        /*DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF(cad);*/
        paquete_datos.flush();
        paquete_datos.close();
        s.close();
    }
    /*
    static void chat(String cad) throws IOException {
        Socket s = new Socket("localhost", 7888);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

//        Ejemplo 1
//        dout.writeUTF("Cliente -  " + (int)(Math.random()*100));

//        Ejemplo 2

        //System.out.print("Mensaje: ");
        dout.writeUTF(cad);
        dout.flush();
        dout.close();
        s.close();
    }*/
}