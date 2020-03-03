package ClientSoft;

import Answers.ClientAnswer;

import java.util.Scanner;

public class UserNavigator {

    private Scanner scanner;
    private String login;
    private ConsoleReader consoleReader;
    private String password;

    public UserNavigator(ConsoleReader consoleReader){
        this.consoleReader = consoleReader;
        scanner = new Scanner(System.in);
    }

    public void logined(){
        System.out.println("----\nВведите логин:");
        login = scanner.nextLine().trim();
        System.out.println("----\nВведите пароль:");
        password = scanner.nextLine().trim();
    }

    public void register(){
            System.out.println("----\nВведите логин:");
            login = scanner.nextLine().trim();
            if (login.contains("@")) {
                ClientAnswer clientAnswer = new ClientAnswer("REGISTER", login, "000000");
                consoleReader.setRegistration( true);
                consoleReader.send(clientAnswer);
            }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
