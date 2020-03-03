package Answers;

import PlantsInfo.Plants;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class ClientAnswer implements Serializable {
    private String answer;
    private ConcurrentSkipListSet<Plants> plants;
    private Plants plant;
    private String login;
    private String password;
    private String paths;
    private static final long serialVersionUID = 1L;

    public ClientAnswer(String msg, String login, String password){
        this.login = login;
        this.password = password;
        this.answer = msg;
    }

    public ClientAnswer(String msg){
        this.answer = msg;
//        this.arg = args;
    }

    public ClientAnswer(Plants plantes, String str, String login, String password){
        this.plant = plantes;
        this.answer = str;
        this.login = login;
        this.password = password;
    }

    public ClientAnswer(ConcurrentSkipListSet<Plants> collection, String message,String login, String password){
        this.plants = collection;
        this.answer = message;
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getPaths() {
        return paths;
    }

    //    public CopyOnWriteArraySet<Plants> getCopyCollection(){
//        return copyCollection;
//    }


    public Plants getPlant() {
        return plant;
    }

    public ConcurrentSkipListSet<Plants> getPlants(){
        return plants;
    }

    public String getAnswer(){
        return answer;
    }

//    public T getArg(){
//        return arg;
//    }

}
