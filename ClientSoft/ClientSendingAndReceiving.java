package ClientSoft;


import Answers.ClientAnswer;
import Answers.ServerAnswer;
import PlantsInfo.Plants;


import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientSendingAndReceiving<T> extends Thread{

    private DatagramChannel datagramChannel;
    private SocketAddress client;
    private boolean exit = false;
    private SocketAddress serv;
    private ConsoleReader reader;
    private ByteBuffer recievBuffer = ByteBuffer.allocate(16384);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(16384);

    public ClientSendingAndReceiving(DatagramChannel channel,ConsoleReader consoleReader,SocketAddress serverAddress,SocketAddress clientAddress){
        this.reader = consoleReader;
        this.datagramChannel = channel;
        this.serv = serverAddress;
        this.client = clientAddress;
    }

//    @Override
//    public void run(){
////        try {
////            datagramChannel.bind(client);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (!isInterrupted() & !exit) {
//                        try {
//                            datagramChannel.connect(serv);
//                            datagramChannel.receive(recievBuffer);
//                            setDaemon(true);
//                            recievBuffer.flip();
//                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(recievBuffer.array());
//                            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//                            ServerAnswer serverAnswer = (ServerAnswer) objectInputStream.readObject();
//                            new AnalyzMessage(reader,serverAnswer);
//                            //Код оповещающий о подключении
//                            objectInputStream.close();
//                            byteArrayInputStream.close();
//                            recievBuffer.clear();
//                            if (serverAnswer.getCommand().equals("DISCONNECT")) {
//                                interrupt();
//                                exit = true;
//                            }
//                            datagramChannel.disconnect();
//                        } catch (IOException | ClassNotFoundException e) {
//                            System.out.println("----\nВозникла ошибка:\n");
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
////        }catch (IOException e){
////            System.out.println("----\nВозникла ошибка:\n");
////            e.printStackTrace();
////        }
//    }





    public void sendMessage(ClientAnswer clientAnswer){
        try {
//                System.out.println("Sending...");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(clientAnswer);
                sendBuffer.put(byteArrayOutputStream.toByteArray());
                objectOutputStream.flush();
                byteArrayOutputStream.flush();
                sendBuffer.flip();
                datagramChannel.send(sendBuffer, serv);
                if (clientAnswer.getAnswer().equals("DISCONNECT")){
                    exit = true;
//                    datagramChannel.close();
                }
                System.out.println("----\nСообщение отправлено.\n----");
                objectOutputStream.close();
                byteArrayOutputStream.close();
                sendBuffer.clear();
        }catch (IOException e){
            System.out.println("----\nВозникла ошибка:\n");
            e.printStackTrace();
        }
    }
}
//-19051151140236710810510111011683111102116466710810510111011665110115119101114000000012017606971101151191011141160187610697118974710897110103478311611410511010359120112116079911111011010199116[B@2f686d1f