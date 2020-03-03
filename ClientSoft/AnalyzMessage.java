package ClientSoft;

import Answers.ServerAnswer;

public class AnalyzMessage {

    private ConsoleReader reader;
    private ServerAnswer answer;

    public AnalyzMessage(ConsoleReader consoleReader, ServerAnswer serverAnswer){
        this.answer = serverAnswer;
        this.reader = consoleReader;
        analyz(answer);

    }

    private void analyz(ServerAnswer serverAnswer){
        switch (serverAnswer.getCommand()){
            case "LOGINMISS":
                reader.setLoginmiss(true);
//                reader.setLogin(false);
                reader.printMessage(serverAnswer);
                break;
            case "CONNECT":
                reader.setConnect(true);
                break;
            case "DISCONNECT":
                reader.setConnect(false);
                reader.setServerDisconnect(true);
                reader.setWork(false);
                break;
            case "LOGINHIT":
                reader.printMessage(serverAnswer);
                break;
            case "HELP":
            case "INFO":
            case "ADD":
            case "ADD_IF_MAX":
            case "REMOVE":
            case "SHOW":
            case "IMPORT":
            case "REMOVE_LOWER":
            case "REMOVE_GREATER":
                reader.printMessage(serverAnswer);
                break;
                default:
                    System.out.println("----\nНеизвестная команда от сервера:\n" + serverAnswer.getCommand() + "\n----");
        }
    }
}
