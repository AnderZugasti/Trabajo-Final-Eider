package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
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
            System.out.println("pasa por server?");
            c = s.accept(); //esperando cliente
            System.out.println("si pasa");

            System.out.println("cliente conectado");
            Hilo hilo = new Hilo(c);
            hilo.start();
        }
    }
}
