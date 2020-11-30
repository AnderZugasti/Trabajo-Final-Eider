package com.company;

import java.io.*;
import java.net.Socket;
import javax.crypto.*;
import java.security.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hilo extends Thread {
    Socket c = new Socket();


    public Hilo(Socket c) {
        this.c = c;

    }

    public void run() {
        int puntuación = 0, pregunta = 0, respCorrec;
        String texto, res1, res2, res3, recogida, respuesta, nombre, apellido, nick, edad, contraseña;
        boolean valido = false;
        Pattern patNombre = Pattern.compile("[a-zA-Z]{2,10}");
        Pattern patApellido = Pattern.compile("[a-zA-Z]{2,15}");
        Pattern patEdad = Pattern.compile("[0-9]{1,3}");
        Pattern patNick = Pattern.compile("[a-zA-z]{1,5}");
        Pattern patContrasenia = Pattern.compile("[A-Z]{2}[a-z0-9]");
        Matcher mat = null;
        KeyPairGenerator keygen = null;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeyPair keypair = keygen.generateKeyPair();

    //Se reciben los datos del jugador
        try {
            System.out.println("Cliente conectado, Se empieza a recoger datos");
            OutputStream aux = c.getOutputStream();
            DataOutputStream flujoSalida = new DataOutputStream(aux);
            InputStream inp = c.getInputStream();
            DataInputStream flujoEntrada = new DataInputStream(inp);

            //Se recoge el nombre, se repite hasta que lo mete bien
            do {
                recogida = "Idatzi izena:";
                flujoSalida.writeUTF(recogida);
                respuesta = flujoEntrada.readUTF();
                nombre = respuesta;
                mat = patNombre.matcher(respuesta);
                if (mat.find()) {
                    valido = true;
                    System.out.println(respuesta + " valido: " + valido);
                }
                flujoSalida.writeBoolean(valido);
            } while (!valido);
            valido = false;

            //Se recoge apellido, se repirte hasta que se meta bien
            do {
                recogida = "Kaixo " + nombre + ",abizena? :";
                flujoSalida.writeUTF(recogida);
                respuesta = flujoEntrada.readUTF();
                apellido = respuesta;
                mat = patApellido.matcher(respuesta);
                if (mat.find()) {
                    valido = true;
                    System.out.println(respuesta + " valido: " + valido);
                }
                flujoSalida.writeBoolean(valido);
            } while (!valido);
            valido = false;

            //Se recoge la edad, se repirte hasta que se meta bien

            do {
                recogida = "Adina:";
                flujoSalida.writeUTF(recogida);
                respuesta = flujoEntrada.readUTF();
                edad = respuesta;
                mat = patEdad.matcher(respuesta);
                if (mat.matches()) {
                    valido = true;
                    System.out.println(respuesta + " valido: " + valido);
                }
                flujoSalida.writeBoolean(valido);
            } while (!valido);
            valido = false;

            //Se recoge el nick, se repirte hasta que se meta bien

            do {
                recogida = "Idatzi zure Nick-a, gehienez 5 letra zenbakirik gabe:";
                flujoSalida.writeUTF(recogida);
                respuesta = flujoEntrada.readUTF();
                nick = respuesta;
                mat = patNick.matcher(respuesta);
                if (mat.matches()) {
                    valido = true;
                    System.out.println(respuesta + " valido: " + valido);
                }
                flujoSalida.writeBoolean(valido);
            } while (!valido);
            valido = false;

            //Se recoge el nick, se repirte hasta que se meta bien

            do {
                recogida = "Pasahitza, gutxienez bi letra larri eta 8 karaktere baino gehio:";
                flujoSalida.writeUTF(recogida);
                respuesta = flujoEntrada.readUTF();
                contraseña = respuesta;
                mat = patContrasenia.matcher(respuesta);
                if (mat.find()) {
                    valido = true;
                    System.out.println(respuesta + " valido: " + valido);
                }
                flujoSalida.writeBoolean(valido);
            } while (!valido);
            valido = false;


            //Se firman las normas del juego

            String normas = "Joko hau “ Egunean behin” jolaserako entrenamendu moduan erabili daiteke, bertan agertzen diren galderekin egin baitut jolasa.\n" +
                    "Kaixo” izena”, jolasten hasi baino lehen jolasaren jarraibideak onartu behar dituzu:\n" +
                    "Galderak:\n" +
                    "Galdera guztiak 3 erantzun posible izango ditu, laugarren aukera jolasetik ateratzeko izango da.\n" +
                    "Galdera bat asmatzeagatik 2 puntu gehituko zaizkizu, baina akats bakoitzagatik puntu bat kenduko zaizu.\n" +
                    "Amaiera:\n" +
                    "Laugarren aukera aukeratzean jolasetik aterako zara eta zure puntuaketa ikusi ahal izango duzu.\n";

            //Se resume el texto(HASH)
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA");
                byte dataBytes[] = normas.getBytes();
                md.update(dataBytes);
                byte resumen[] = md.digest();

                //Se pasa a Hexadecimal

                String hex = "";
                for (int i = 0; i < resumen.length; i++) {
                    String h = Integer.toHexString(resumen[i] & 0xF);
                    if (h.length() == 1) {
                        hex += "0";
                    }
                    hex += h;
                }
                String Hexadecimal = hex.toUpperCase();
                System.out.println(Hexadecimal);
                //TODO generar claves pública y privada, encriptar hash y mandar encriptado y texto asecas

                //Se generan las claves pública y privada

            try{

                Signature dsa = Signature.getInstance("SHA1WITHRSA");
                dsa.initSign(keypair.getPrivate());
                dsa.update(Hexadecimal.getBytes());
                byte[] firma = dsa.sign();
                //Se envian clave pública, normas y el HexaCifrado
                System.out.println("Se le envía a "+nombre+" las normas");
                flujoSalida.writeUTF(normas);//normas
                ObjectOutputStream oos = new ObjectOutputStream(c.getOutputStream());
                oos.writeObject(keypair.getPublic());//clave pública
                oos.writeObject(firma);
                int eleccion = flujoEntrada.readInt();
                if ( eleccion ==1){
                    juego(c);
                }else {
                    c.close();
                }


            }catch (Exception b){

            }


            } catch (Exception a) {

            }


        } catch (Exception e) {

        }

    }
    /** La función juego contiene el código para comenzar a jugar
     * @param c es el socket que nos llega de la conexión
     * **/
    public static void juego(Socket c){
        int puntuacion;
        //Todo empezar con las preguntas
    }

}
