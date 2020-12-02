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
    static boolean conexion = false;

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        //Conectamos al cliente
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
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                    if (check) {
                        int eleccion;
                        do {

                            System.out.println(normas + "\n Konexio fidagarria daukazu, jolastu nahi duzu?\n1-Bai\n2-Ez");
                            eleccion = Integer.parseInt(br.readLine());
                        } while (eleccion != 1 && eleccion != 2);
                        if (eleccion == 1) {
                            oos.writeObject(eleccion);
                            System.out.println(eleccion);
                            juego(socket);
                            //socket.close();
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
        String preguntaD, res1D, res2D, res3D;
        byte []  pregunta, res1, res2, res3;


        //Genero las claves pública y privada
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(1024);
        KeyPair keyPair = keygen.generateKeyPair();

        PrivateKey prvKey = keyPair.getPrivate();
        PublicKey pblKey = keyPair.getPublic();
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        //Envio clave publica al server
        oos.writeObject(pblKey);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        //Todo empezar con las preguntas
        do {
            //Recojo la informacion
            System.out.println("Preparo para desencriptar");
            PublicKey claveServer = (PublicKey) ois.readObject();
            pregunta = (byte[]) ois.readObject();
            res1 = (byte[]) ois.readObject();
            res2 = (byte[]) ois.readObject();
            res3 = (byte[]) ois.readObject();

            //Descifro los textos
            try {
                Cipher des = Cipher.getInstance("RSA");
                System.out.println("empiezo a desencriptar");
                des.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                System.out.println("pasa por auí");
                String preguntaB = new String(des.doFinal(pregunta));
                System.out.println(1);
                String res1B = new String(des.doFinal(res1));
                String res2B = new String(des.doFinal(res2));
                String res3B = new String(des.doFinal(res3));
                System.out.println("desencriptado");
                System.out.println(preguntaB + "\n1-" + res1B + "\n2-" + res2B + "\n3-" + res3B + "\n4- Amaitu");
                String seleccion = (br.readLine());
                System.out.println(seleccion);
                respuesta = Integer.parseInt(seleccion);
                //Encripto la respuesta
                des.init(Cipher.ENCRYPT_MODE, claveServer);
                System.out.println("se envia respuesta");
                byte[] elecE = des.doFinal(seleccion.getBytes());
                ObjectOutputStream ooo = new ObjectOutputStream(socket.getOutputStream());
                ooo.writeObject(elecE);
                System.out.println(elecE);
            } catch (Exception b) {
                System.err.println(b);
            }


        } while (respuesta != 4);

    }
}