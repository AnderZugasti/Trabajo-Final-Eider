package com.company;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Hilo extends Thread {
    Socket c = new Socket();


    public Hilo(Socket c) {
        this.c = c;

    }

    public void run() {
        int puntuación=0,pregunta=0,respCorrec;
        String texto, res1,res2,res3;


        try {
            //Generamos el par de claves
            KeyPairGenerator keygen;

            keygen = KeyPairGenerator.getInstance("RSA");

            System.out.println("Generando par de claves");
            KeyPair par = keygen.generateKeyPair();
            PrivateKey privada=par.getPrivate();
            PublicKey publica=par.getPublic();
            //Creamos los flujos
            ObjectOutputStream oos = new ObjectOutputStream(c.getOutputStream());
            //mandamos la clave publica

            oos.writeObject(publica);
            System.out.println("Enviamos la clave publica cuyo valor es: "+publica);


        } catch (Exception e) {

        }

    }
}
