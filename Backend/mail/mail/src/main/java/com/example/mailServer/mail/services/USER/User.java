package com.example.mailServer.mail.services.USER;

import com.example.mailServer.mail.services.Contact;
import com.example.mailServer.mail.services.Email.Email;
import com.example.mailServer.mail.services.Folder;

import java.util.ArrayList;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,include = JsonTypeInfo.As.PROPERTY, visible = true)
//@JsonIgnoreProperties(value = "attributes",ignoreUnknown = true)

public class User implements IUser , Acceptable{
    // primary data for user
    private String username ;
    private String emailaccount ;
    private String password ;
    private String birth ;
    private ArrayList<Folder> folders;
    private ArrayList<Contact> contacts ;
    private int idMessage ;


    public User(UserDTO u){
        this.birth = u.birth ;
        this.emailaccount = u.emailaccount ;
        this.password = u.password ;
        this.username = u.username ;
        this.folders = new ArrayList<>() ;
        this.contacts = new ArrayList<>() ;
        this.idMessage = 0 ;
    }
    public User(){}

    public String getBirth() {
        return birth;
    }

    public String getEmailaccount() {
        return emailaccount;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setEmailaccount(String emailaccount) {
        this.emailaccount = emailaccount;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }
    public void addFolder(String name ){
        folders.add(new Folder(new ArrayList<Email>(),name)) ;
        //System.out.println(this.folders.get(this.folders.size()-1).getName());
        return ;
    }
    public void addEmailToFolder(String name , Email email){
        for(Folder f : getFolders()){
            if(f.getName().equals(name)) {
                f.addEmail(email);
                return ;
            }
        }
    }
    public void addContact(String name , String email ,int id ){
        contacts.add(new Contact(email ,name , id)) ;

    }




    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", emailaccount='" + emailaccount + '\'' +
                ", password='" + password + '\'' +
                ", birth='" + birth + '\'' +
                ", folders=" + folders +
                ", contacts=" + contacts +
                ", idMessage=" + idMessage +
                '}';
    }
}
