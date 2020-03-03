package ClientSoft;

import Answers.*;
import Parser.*;
import PlantsInfo.Plants;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListSet;

public class ConsoleReader {

    private int PORT_CLIENT = 3333;
    private ClientSendingAndReceiving sendingAndReceiving;
    private boolean workable = false;
    private Scanner scanner;
    private Receiver receiver;
    private boolean serverDisconnect = false;
    private DatagramChannel channel;
    private boolean loaded = false;
    private SocketAddress socketAddress;
    private UserNavigator userNavigator;
    private boolean exit = true;
    private SocketAddress address;
    private int time = 0;
    private boolean loginmiss = false;
    private boolean portSet = false;
    private boolean registration = false;
    private boolean connect = false;
    private boolean login = false;
    private boolean work = true;

    public ConsoleReader(){
        setWorkable(true);
        scanner = new Scanner(System.in);
        socketAddress = new InetSocketAddress("localhost", PORT_CLIENT);
    }

    public void setServerDisconnect(boolean serverDisconnect) {
        this.serverDisconnect = serverDisconnect;
    }

    public boolean getLoginMiss(){
        return loginmiss;
    }

    public void setLoginmiss(boolean loginmiss) {
        this.loginmiss = loginmiss;
    }

    protected void setPort(){
        portSet = false;
        System.out.println("----\nУкажите порт для подключения к серверу\n----");
        while (!portSet) {
            String numb = scanner.nextLine();
            if (numb.matches("[0-9]+")) {
                if (Integer.parseInt(numb) < 65535 & Integer.parseInt(numb) != 5432) {
                    address = new InetSocketAddress("localhost", Integer.parseInt(numb));
                    portSet = true;
                } else {
                    System.out.println("----\nНедопустимый номер порта, введите снова\n----");
                    continue;
                }
            } else {
                System.out.println("----\nНедопустимый номер порта, введите снова\n----");
                continue;
            }
        }

    }

    public void setWork(boolean work) {
        this.work = work;
    }

    public void setWorkable(boolean workable){
        this.workable = workable;
    }

    public void setConnect(boolean var){
        this.connect = var;
    }

    public boolean getConnect(){
        return connect;
    }

    public void checkMode(){
        while (work) {
            System.out.println("----\nРабота ведется в онлайн режиме с подключением клиента\n" +
                    "Для начала работы введите \"start\", для завершения введите \"exit\".\n---- ");
            String answer = scanner.nextLine();
            switch (answer.trim()){
                case "exit":
                    setWorkable(false);
                    work = false;
                    System.out.println("----\nЗавершение работы...\n----");

                    break;
                case "start":
                    work = true;
                    try {
                        channel = DatagramChannel.open();
                        setPort();
                        receiver = new Receiver(channel,this,address,socketAddress);
                        receiver.setDaemon(true);
                        receiver.start();
                        sendingAndReceiving = new ClientSendingAndReceiving(channel,this,address,socketAddress);
//                        sendingAndReceiving.start();
                        sendingAndReceiving.sendMessage(new ClientAnswer("CONNECT"));
                        if (!getConnect()) {
                            while ((time < 10000) && !getConnect()) {
                                Thread.sleep(1000);
                                System.out.println("----\nОжидание...\n----");
                                time += 1000;
                            }
                        }
                        if (workable && getConnect()) {
                            System.out.println("----\nСоединение установлено\n----");
                            afterConnect();
                        } else {
                            portSet = false;
                            setConnect(false);
                            System.out.println("----\nОтвет от сервера не получен. \nВозможно ответ придёт позже. \nПопробуйте повторить попытку соединенияё\n----");
                        }
                    }catch (IOException | InterruptedException e){
                        System.out.println("----\nВозникла ошибка:");
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("----\nНераспознаный ответ, введите снова\n----");
                    continue;
            }
        }
    }

    public void printMessage(ServerAnswer serverAnswer){
        System.out.println(serverAnswer.getAnswer());
    }

    private void shootDownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!exit) {
                sendingAndReceiving.sendMessage(new ClientAnswer("DISCONNECT"));
                System.out.println("----\nЗавершение работы\n----");
            }

        }));
    }


    public void logined(){
        System.out.println("----\nЕсли вы уже имеете учётную запись введите \"login\",\nесли вы здесь впервые введите \"register\"\n----");
        userNavigator = new UserNavigator(this);
        while (!login){
            switch (scanner.nextLine()){
                case "login":
                    userNavigator.logined();
                    login = true;
                    break;
                case "register":
                    userNavigator.register();
                    login = true;
                    break;
                    default:
                        System.out.println("----\nНераспознаный ответ, повторите ввод.\n----");
                        continue;
            }
        }
    }

    public String autorizationLogin(){
        System.out.println("----\nВведите ваш логин:");
        return scanner.nextLine();
    }

    public String autorizationPassword(){
        System.out.println("----\nВведите ваш пароль:");
        return scanner.nextLine();
    }

    public void work(){
        setWorkable(true);
        System.out.println("----\nНачало работы\n----");
        checkMode();
    }

    public void afterConnect(){
        shootDownHook();
        while (work){
            if (workable & connect) {
                scanAndExit();
            }
            else{
                checkMode();
            }
        }
        if (serverDisconnect) {
            System.out.println("----\nСервер отключился, для продолжения работы перезапустите клиент.\n" +
                    "Для завершения введите \"exit\".\n----");
            while (exit){
                if (scanner.nextLine().equals("exit")){
                    exit =false;
                }
                else {
                    System.out.println("----\nНераспознаный ответ\n----");
                }
            }
        }
    }

    public void send(String command){
        logined();
        login = false;
        if (!registration) {
            sendingAndReceiving.sendMessage(new ClientAnswer(command, userNavigator.getLogin(), userNavigator.getPassword()));
        }
        registration = false;
    }

    public void send(Plants plants,String command){
        logined();
        login =false;
        if (!registration) {
            sendingAndReceiving.sendMessage(new ClientAnswer(plants, command, userNavigator.getLogin(), userNavigator.getPassword()));
        }
        registration = false;
    }

    public void send(ConcurrentSkipListSet<Plants> plants, String command) {
        logined();
        login = false;
        if (!registration) {
            sendingAndReceiving.sendMessage(new ClientAnswer(plants, command, userNavigator.getLogin(), userNavigator.getPassword()));
        }
        registration =false;
    }

    public void send(ClientAnswer clientAnswer){
        login = false;
        sendingAndReceiving.sendMessage(clientAnswer);

    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public void scanAndExit(){
        try{
            String command[] = new String[50];
            if (!loaded) {
            command[0] = scanner.nextLine();
            String helpcom[] = command[0].trim().split(" ",2);
                switch (helpcom[0].trim()) {
                    case "info":
                        if (helpcom.length > 1) {
                            if (helpcom[1].matches(" +") | helpcom[1].matches("")) {
                                System.out.println("----\nДанная команда не должна содержать аргументов.\n----");
                            }
                        } else {
                            send("INFO");
                        }
                        break;
                    case "register":
                        if (helpcom.length >1){
                            if (helpcom[1].matches(" +") | helpcom[1].matches("")) {
                                System.out.println("----\nДанная команда не должна содержать аргументов.\n----");
                            }
                        }else
                            new UserNavigator(this).register();
                        break;
                    case "show":
                        if (workable) {
                            if (helpcom.length > 1) {
                                System.out.println("----\nВ данной команде не должно быть аргументов\n----");
                            } else
                                send("SHOW");
                        }
                        break;
                    case "add":
                        try {
                            if (getBracket(helpcom[1], '}') == 2) {
                                send(getElements(helpcom[1]), "ADD");
                            } else {
                                Plants plants = getElements(scanElements(helpcom[1]));
                                send(plants,"ADD");
                            }
                            System.out.println("\n----");
                        } catch (JSONException e) {
                            System.out.println("----\nОбнаружена ошибка при парсинге элемента: " + e.getMessage() + "\n----");
                        }
                        break;
                    case "add_if_max":
                        try {
                            if (getBracket(helpcom[1], '}') == 2) {
                                send(getElements(helpcom[1]), "ADD_IF_MAX");
                            } else {
                                Plants plants = getElements(scanElements(helpcom[1]));
                                send(plants, "ADD_IF_MAX");
                            }
                            System.out.println("\n----");
                        } catch (JSONException e) {
                            System.out.println("----\nОбнаружена ошибка при парсинге элемента: " + e.getMessage() + "\n----");
                        }
                        break;
                    case "remove":
                        try {
                            if (getBracket(helpcom[1], '}') == 2) {
                                send(getElements(helpcom[1]), "REMOVE");
                            } else {
                                Plants plants = getElements(scanElements(helpcom[1]));
                                send(plants,"REMOVE");
                            }
                            System.out.println("\n----");
                        } catch (JSONException e) {
                            System.out.println("----\nОбнаружена ошибка при парсинге элемента: " + e.getMessage() + "\n----");
                        }
                        break;
                    case "remove_greater":
                        try {
                            if (getBracket(helpcom[1], '}') == 2) {
                                send(getElements(helpcom[1]), "REMOVE_GREATER");
                            } else {
                                Plants plants = getElements(scanElements(helpcom[1]));
                                send(plants, "REMOVE_GREATER");
                            }
                            System.out.println("\n----");
                        } catch (JSONException e) {
                            System.out.println("----\nОбнаружена ошибка при парсинге элемента: " + e.getMessage() + "\n----");
                        }
                        break;
                    case "remove_lower":
                        try {
                            if (getBracket(helpcom[1], '}') == 2) {
                                send(getElements(helpcom[1]), "REMOVE_LOWER");
                            } else {
                                Plants plants = getElements(scanElements(helpcom[1]));
                                send(plants, "REMOVE_LOWER");
                            }
                            System.out.println("\n----");
                        } catch (JSONException e) {
                            System.out.println("----\nОбнаружена ошибка при парсинге элемента: " + e.getMessage() + "\n----");
                        }
                        break;
                    case "exit":
                        if (workable)
                        work = false;
                        workable = false;
                        break;
                    case "import":
                        if (helpcom.length > 1) {
                            if (helpcom[1].matches(" +") | helpcom[1].matches("")) {
                                System.out.println("----\nДанная команда не должна содержать аргументов.\n----");
                            }
                        }else {
                            if (workable) {
                                Loader loader = new Loader();
                                if (loader.getOK()) {
                                    send(loader.getLinkHSPlants(), "IMPORT");
                                }
                            }
                        }
                        break;
                    case "help":
                        if (workable)
                            send("HELP");
                        break;
                    default:
                        if (workable)
                            System.out.println("----\nНеизвестная команда.\n----");
                        break;
                }
            }
    }catch (ArrayIndexOutOfBoundsException e){
        System.out.println("----\nОшибка ввода элемента.\n----");
    }
    }

    private String scanElements(String helpcom){
        int rightbrecket = 0;
        int leftbrecket = 0;
        String command[] = new String[15];
        int k = 0;
        String plz = helpcom;
        while(leftbrecket != rightbrecket ) {
            command[k] = scanner.nextLine();
            command[k].trim();
            rightbrecket += getBracket(command[k],'}');
            leftbrecket += getBracket(command[k],'{');
            plz += command[k];
            k++;
            if (k == 11){
                System.out.println("----\nОшибка ввода элемента.\n----");
                break;
            }
        }
        return plz;
    }


    private Plants getElements(String txt){
        int countright = getBracket(txt,'}');
        int countleft = getBracket(txt, '{');
        while (!(countleft == countright)){
            String str = scanner.nextLine();
            countright += getBracket(str , '}');
            countleft += getBracket(str, '{');
            txt += str;
        }
        txt = txt.trim();
        JSONParser jsonParser = new JSONParser();
        return jsonParser.objParse(txt);
    }

    private int getBracket(String str,char bracket){
        int count = 0;
        for(char c : str.toCharArray()){
            if (c == bracket)
                count++;
        }
        return count;
    }
}
