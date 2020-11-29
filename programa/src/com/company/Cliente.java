package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class Cliente extends Thread {
    static int puerto = 5000;

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        //Conectamos al cliente
        boolean conexion = false;
        while (!conexion) {
            try {
                System.out.println("paso 0");
                Socket socket = new Socket("localhost", puerto);
                System.out.println("paso 1 "+ puerto);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("pasa3");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("paso 2");
                System.out.println("Leemos la clave");
                //obtenemos la clave publica
                PublicKey clave=(PublicKey) ois.readObject();
                conexion = true;

            } catch (Exception e) {
                puerto++;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("cliente conectado en el puerto: "+puerto);
            br.readLine();
        }
    }
}