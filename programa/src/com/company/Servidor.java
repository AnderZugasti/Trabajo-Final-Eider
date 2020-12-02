package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Servidor {
     static int puerto = 5500;


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, ClassNotFoundException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {

        ServerSocket s;
        Socket c;
        s = new ServerSocket(5000);
        System.out.println("Servidor iniciado");
        while (true) {
            c = s.accept(); //esperando cliente
            System.out.println("cliente conectado");
            ObjectInputStream ois = new ObjectInputStream(c.getInputStream());

            ObjectOutputStream oos = new ObjectOutputStream(c.getOutputStream());
            Hilo hilo = new Hilo(c,oos,ois);
            hilo.start();
        }
    }
}
