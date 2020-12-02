package com.company;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.ArrayList;

public class Cliente extends Thread {
    static int puerto = 5000;

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        //Conectamos al cliente
        boolean conexion = false;
        String recogida, envio, normas;
        Boolean valido = false;
        Socket socket;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!conexion) {
            try {
                socket = new Socket("localhost", puerto);
                InputStream aux = socket.getInputStream();
                DataInputStream flujoEntrada = new DataInputStream(aux);
                OutputStream out = socket.getOutputStream();
                DataOutputStream flujoSalida = new DataOutputStream(out);

                //se envia el nombre, si no es valido se repite la acción
                do {
                    recogida = flujoEntrada.readUTF();
                    System.out.println(recogida);
                    envio = br.readLine();
                    flujoSalida.writeUTF(envio);
                    valido = flujoEntrada.readBoolean();
                } while (!valido);


                //Se envía apellido, si no es valido se repite la acción

                do {
                    recogida = flujoEntrada.readUTF();
                    System.out.println(recogida);
                    envio = br.readLine();
                    flujoSalida.writeUTF(envio);
                    valido = flujoEntrada.readBoolean();
                } while (!valido);


                //Se envía edad, si no es valido se repite la acción

                do {
                    recogida = flujoEntrada.readUTF();
                    System.out.println(recogida);
                    envio = br.readLine();
                    flujoSalida.writeUTF(envio);
                    valido = flujoEntrada.readBoolean();
                } while (!valido);

                //Se envía nick, si no es valido se repite la acción

                do {
                    recogida = flujoEntrada.readUTF();
                    System.out.println(recogida);
                    envio = br.readLine();
                    flujoSalida.writeUTF(envio);
                    valido = flujoEntrada.readBoolean();
                } while (!valido);

                //Se envía contraseña, si no es valido se repite la acción

                do {
                    recogida = flujoEntrada.readUTF();
                    System.out.println(recogida);
                    envio = br.readLine();
                    flujoSalida.writeUTF(envio);
                    valido = flujoEntrada.readBoolean();
                } while (!valido);

                //Todo recibir texto, hash encriptado y clave pública, haceptar los requisitos, si no, se termina
                //Se reciben las normas
                normas = flujoEntrada.readUTF();
                //Se recibe clave pública
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                PublicKey claveP = (PublicKey) ois.readObject();
                //Se recive el hash firmado
                byte[] firma = (byte[]) ois.readObject();
                //Se saca el resumen de las normas para sacar el hash
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
                    //se verifica la firma comparando el resumen generado y el resumen que no ha llegado firmado.
                    Signature verificadsa = Signature.getInstance("SHA1WITHRSA");
                    verificadsa.initVerify(claveP);
                    verificadsa.update(Hexadecimal.getBytes());
                    boolean check = verificadsa.verify(firma);
                    System.out.println(check);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    if (check) {
                        int eleccion;
                        do {

                            System.out.println(normas + "\n Konexio fidagarria daukazu, jolastu nahi duzu?\n1- Bai\n2-Ez");
                            eleccion = Integer.parseInt(br.readLine());
                        } while (eleccion != 1 && eleccion != 2);
                        if (eleccion == 1) {
                            System.out.println("pasa");
                            System.out.println();
                            oos.writeObject(eleccion);
                            System.out.println(eleccion);
                            juego(socket);
                        } else {
                            oos.writeObject(eleccion);
                            System.out.println("hurrengorate orduan!!");
                            socket.close();
                        }

                    } else {
                        oos.writeObject(2);
                        System.out.println("Konexioa ez da fidagarria, konexioa hizten");
                        socket.close();
                    }

                } catch (Exception a) {

                }
            } catch (Exception e) {

            }

        }
    }

    public static void juego(Socket socket) throws IOException, ClassNotFoundException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int respuesta = 0;
        String preguntaD, res1D, res2D, res3D,pregunta, res1, res2, res3;;


        //Genero las claves pública y privada
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(1024);
        System.out.println("Genero claves");
        KeyPair keyPair = keygen.generateKeyPair();

        PrivateKey prvKey = keyPair.getPrivate();
        PublicKey pblKey = keyPair.getPublic(), claveServer;
        System.out.println("entra en la función");
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        //Envio clave publica al server
        oos.writeObject(pblKey);
        System.out.println("Envio clave");

        //Todo empezar con las preguntas
        do {
            //Recojo la informacion
            System.out.println("Recojo preguntas");

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            claveServer = (PublicKey) ois.readObject();
            pregunta = (String) ois.readObject();
            res1 = (String) ois.readObject();
            res2 = (String) ois.readObject();
            res3 = (String) ois.readObject();
            System.out.println("Recojo preguntas2");

            //Descifro los textos
            try {
                Cipher des = Cipher.getInstance("RSA");
                System.out.println("empiezo a desencriptar");
                des.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

                String preguntaB = new String(des.doFinal(pregunta.getBytes()));
                System.out.println(1);
                String res1B = new String(des.doFinal(res1.getBytes()));
                String res2B = new String(des.doFinal(res2.getBytes()));
                String res3B = new String(des.doFinal(res3.getBytes()));
                System.out.println("desencriptado");
                System.out.println(preguntaB + "\n1-" + res1B + "\n2-" + res2B + "\n3-" + res3B + "\n4- Amaitu");
                String seleccion =(br.readLine());
                respuesta = Integer.parseInt(seleccion);
                //Encripto la respuesta
                Cipher desRes = Cipher.getInstance("AES");
                desRes.init(Cipher.ENCRYPT_MODE, claveServer);
                byte[] elecE= desRes.doFinal(seleccion.getBytes());
                oos.writeObject(elecE);
            }catch (Exception b){
                System.out.println(b);
            }



        } while ( respuesta!= 4);


    }
}