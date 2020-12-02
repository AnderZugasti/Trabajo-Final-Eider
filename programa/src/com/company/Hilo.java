package com.company;

import java.io.*;
import java.net.Socket;
import javax.crypto.*;
import java.security.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hilo extends Thread {
    Socket c = new Socket();
    private  String texto;
    private  String res1;
    private  String res2;
    private  String res3;
    private static int respCorrec, puntuacion, correctas, incorrectas;
    private static KeyPairGenerator keygen = null;
    private static PublicKey Pubkey;
    private static PrivateKey prvKey;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public Hilo(Socket c,ObjectOutputStream oos,ObjectInputStream ois) {
        this.c = c;
        this.ois = ois;
        this.oos= oos;

    }

    public void run() {
        int puntuación = 0, pregunta = 0;
        String recogida, respuesta, nombre, apellido, nick, edad, contraseña;
        boolean valido = false;
        Pattern patNombre = Pattern.compile("[a-zA-Z]{2,10}");
        Pattern patApellido = Pattern.compile("[a-zA-Z]{2,15}");
        Pattern patEdad = Pattern.compile("[0-9]{1,3}");
        Pattern patNick = Pattern.compile("[a-zA-z]{1,5}");
        Pattern patContrasenia = Pattern.compile("[A-Z]{2}[a-z0-9]");
        Matcher mat = null;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeyPair keypair = keygen.generateKeyPair();
        prvKey = keypair.getPrivate();
        Pubkey = keypair.getPublic();

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

            String normas = "Joko hau “Egunean behin” jolaserako entrenamendu moduan erabili daiteke, bertan agertzen diren galderekin egin baitut jolasa.\n" +
                    "Kaixo " + nombre + ", jolasten hasi baino lehen jolasaren jarraibideak onartu behar dituzu:\n" +
                    "\nGalderak:\n" +
                    "Galdera guztiak 3 erantzun posible izango ditu, laugarren aukera jolasetik ateratzeko izango da.\n" +
                    "Galdera bat asmatzeagatik 2 puntu gehituko zaizkizu, baina akats bakoitzagatik puntu bat kenduko zaizu.\n" +
                    "\nAmaiera:\n" +
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

                try {

                    Signature dsa = Signature.getInstance("SHA1WITHRSA");
                    dsa.initSign(keypair.getPrivate());
                    dsa.update(Hexadecimal.getBytes());
                    byte[] firma = dsa.sign();
                    //Se envian clave pública, normas y el HexaCifrado
                    System.out.println("Se le envía a " + nombre + " las normas");
                    flujoSalida.writeUTF(normas);//normas
                   // ObjectOutputStream oos = new ObjectOutputStream(c.getOutputStream());
                    oos.writeObject(keypair.getPublic());//clave pública
                    oos.writeObject(firma);

                    //Se lee la respuesta del cliente
                   // ObjectInputStream ois = new ObjectInputStream(c.getInputStream());

                    int eleccion = (int) ois.readObject();
                    System.out.println("su respuesta es:" + eleccion);
                    if (eleccion == 1) {
                        System.out.println("entra");
                        juego(c, keypair.getPrivate(), keypair.getPublic());
                    }
                    System.out.println("se cierra conexión con " + nombre);
                    c.close();


                } catch (Exception b) {

                }


            } catch (Exception a) {

            }


        } catch (Exception e) {

        }

    }

    /**
     * La función juego contiene el código para comenzar a jugar
     *
     * @param c es el socket que nos llega de la conexión
     **/
    public  void juego(Socket c, PrivateKey prK, PublicKey puK) throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        int cont = 1;
        String respuesta;
      //  ObjectInputStream ois = new ObjectInputStream(c.getInputStream());
        PublicKey claveCliente = (PublicKey) ois.readObject();
        //Todo empezar con las preguntas
        do {
            System.out.println("contador: " + cont);
            switch (cont) {
                case 1:
                    Pregunta1();
                case 2:
                    Pregunta2();
                case 3:
                    Pregunta3();
                case 4:
                    Pregunta4();
                case 5:
                    Pregunta5();
                case 6:
                    Pregunta6();
                case 7:
                    Pregunta7();
                case 8:
                    Pregunta8();
                case 9:
                    Pregunta9();
                case 10:
                    Pregunta10();
            }
            cont++;
            System.out.println("contador: " + cont);
            respuesta = Preguntas(texto, res1, res2, res3, c, prK, puK,claveCliente);
            System.out.println("respuesta: "+respuesta);
            if(respCorrec == Integer.parseInt(respuesta)){
                puntuacion = puntuacion+2;
                correctas++;
            }else{
                incorrectas++;
            }

        } while (!respuesta.equals("4")  || cont <= 10);

    }

    /**
     * Se le envían las preguntas cifradas al cliente
     *
     * @param texto el texto de la pregunta
     * @param res1  texto de la primera respuesta
     * @param res2  texto de la primera respuesta
     * @param res3  texto de la primera respuesta
     * @param socket conexion
     * @param puK La clave publica del servidor
     * @param claveCliente La clave pública del cliente
     **/
    public String Preguntas(String texto, String res1, String res2, String res3, Socket socket, PrivateKey prK, PublicKey puK,PublicKey claveCliente) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {
        System.out.println("entra en la función");
        System.out.println("recojo clave");
        //Se genera Cipher

        System.out.println("Se prepara para encriptar");
        try {
            Cipher des = Cipher.getInstance("RSA");
            des.init(Cipher.ENCRYPT_MODE, claveCliente);
            //Se encriptan los 4 textos




            byte[] textoE = (des.doFinal(texto.getBytes()));
            System.out.println(texto);
            System.out.println(textoE);
            System.out.println(textoE.length);
            byte[] res1E = (des.doFinal(res1.getBytes()));
            byte[] res2E = (des.doFinal(res2.getBytes()));
            byte[] res3E =(des.doFinal(res3.getBytes()));
            //Se envía los 4 textos, la clave pública y el chiper
            System.out.println("Pasa");

           // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(puK);
            oos.writeObject(textoE);
            oos.writeObject(res1E);
            oos.writeObject(res2E);
            oos.writeObject(res3E);
            System.out.println("pasa2");
            System.out.println("Se recoge");
           // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            System.out.println("pasa3");

            byte[] resp = (byte[]) ois.readObject();
            System.out.println(resp);
            System.out.println("se desencripta");
            des.init(Cipher.DECRYPT_MODE,prK);
            return new String (des.doFinal(resp));
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;


    }



    public  void Pregunta1() {
        texto = "Istorioa kontatzen duen pertsonaia....";
        res1 = "Gidoilaria";
        res2 = "Narratzailea";
        res3 = "Protaginista";
        respCorrec = 2;

    }

    public void Pregunta2() {
        texto = "Sinonimoak, zein da okerra?";
        res1 = "Aldia - Garaia";
        res2 = "Umorea - Aldartea";
        res3 = "Laguntza - Gogoa";
        respCorrec = 3;

    }

    public void Pregunta3() {
        texto = "Euskal elkarteen federazioaren izena";
        res1 = "UZEI";
        res2 = "Euskaltzaleen topagunea";
        res3 = "Txioak";
        respCorrec = 2;

    }

    public void Pregunta4() {
        texto = "Noiz ospatzen dira San Lorentzoak?";
        res1 = "Abuztuak 10";
        res2 = "Abuztuak 11";
        res3 = "Abuztuak 12";
        respCorrec = 1;

    }

    public void Pregunta5() {
        texto = "Zein herrialdek du azalerarik handiena";
        res1 = "Guinea Bisau";
        res2 = "Bielorrusia";
        res3 = "Belgika";
        respCorrec = 2;

    }

    public void Pregunta6() {
        texto = "Non dago Ibarrola herria?";
        res1 = "Gipuzkoa";
        res2 = "Behe Nafarroa";
        res3 = "Araba";
        respCorrec = 2;

    }

    public void Pregunta7() {
        texto = "Berria egunkariaren Podkast feminista";
        res1 = "Berria FM";
        res2 = "Emakunde";
        res3 = "Xerezaderen Artxiboa";
        respCorrec = 1;

    }

    public void Pregunta8() {
        texto = "Zein dago gaizki idatzita?";
        res1 = "Biologolaria";
        res2 = "Zientzialaria";
        res3 = "Historialaria";
        respCorrec = 2;

    }

    public void Pregunta9() {
        texto = "Egun hartan itsasoa ..... zegoen.";
        res1 = "zurrun";
        res2 = "bare";
        res3 = "motel";
        respCorrec = 2;

    }

    public void Pregunta10() {
        texto = "Lehenengoari beti .... bigarrena";
        res1 = "darraio";
        res2 = "darraikio";
        res3 = "jarraitzen dio";
        respCorrec = 2;

    }


}
