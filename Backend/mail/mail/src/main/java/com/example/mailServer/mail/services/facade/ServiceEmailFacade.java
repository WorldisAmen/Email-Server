package com.example.mailServer.mail.services.facade;

import com.example.mailServer.mail.services.Contact;
import com.example.mailServer.mail.services.DataCache.Data;
import com.example.mailServer.mail.services.DataCache.DataHelper;
import com.example.mailServer.mail.services.Email.Email;
import com.example.mailServer.mail.services.Email.EmailBuilder;
import com.example.mailServer.mail.services.FilterContact.ContactFactory;
import com.example.mailServer.mail.services.FilterContact.ICriteriaContact;
import com.example.mailServer.mail.services.FilterEmail.CriteriaFactory;
import com.example.mailServer.mail.services.FilterEmail.ICriteria;
import com.example.mailServer.mail.services.Folder;
import com.example.mailServer.mail.services.SortEmail.SortFactory;
import com.example.mailServer.mail.services.SortEmail.Strategy;
import com.example.mailServer.mail.services.USER.Acceptable;
import com.example.mailServer.mail.services.USER.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServiceEmailFacade {
    private Data d = Data.getInstance() ;

    public ServiceEmailFacade() throws IOException {
    }
    public void sendEmail(EmailBuilder e) throws IOException, ParseException {
        Email email = e.build();
        User s = DataHelper.getUserByAccount(email.getSender());
        if (s != null) {
            email.setRead(true);
            email.setId(s.getIdMessage());
            s.addEmailToFolder("sent", email);
            s.setIdMessage(s.getIdMessage()+1);
            for (int i = 0; i < email.getRecievers().size(); i++) {
                User r = DataHelper.getUserByAccount(email.getRecievers().get(i));
                if(r != null){
                    Email email2 = e.build();
                    email2.setRead(false);
                    email2.setId(r.getIdMessage());
                    r.addEmailToFolder("inbox", email2);
                    r.setIdMessage(r.getIdMessage()+1);
                }
            }
            d.saveToJson();
        }
    }
    public List<Email> makeRead (String account ,String folder,  int id  ) throws IOException {
        User u = DataHelper.getUserByAccount(account) ;
        for(Folder f : u.getFolders()){
            if(f.getName().equals(folder)){
                for(Email e : f.getEmails())
                    if(e.getId() == id ){
                        e.setRead(true);
                        d.saveToJson();
                        return  f.getEmails() ;
                    }
                }
        }
        return  null ;
    }
    public List<Email> saveAsDraft(EmailBuilder e ) throws IOException {
        Email email = e.build();
        User u = DataHelper.getUserByAccount(email.getSender());
        if(u != null){
            email.setRead(true);
            email.setId(u.getIdMessage());
            u.addEmailToFolder("draft", email);
            u.setIdMessage(u.getIdMessage()+1);
            d.saveToJson();
            return u.getFolders().get(1).getEmails() ;
        }
        return null ;
    }
    public List<Email> modifyDraft(EmailBuilder e ) throws IOException {
        Email email = e.build();
        email.setRead(true);
        User u = DataHelper.getUserByAccount(email.getSender());
        if(u != null){
            for(int i = 0 ; i < u.getFolders().get(1).getEmails().size() ; ++i ){
                if(u.getFolders().get(1).getEmails().get(i).getId() == e.getId()){
                    u.getFolders().get(1).getEmails().set(i , email) ;
                    d.saveToJson();
                    return u.getFolders().get(1).getEmails() ;
                }
            }
        }
        return null ;
    }
    public List<Email> sendDraftToSent(EmailBuilder e) throws IOException{
        Email email = e.build();
        email.setRead(true);
        User u = DataHelper.getUserByAccount(email.getSender());
        if(u != null){
            for(int i = 0 ; i < u.getFolders().get(1).getEmails().size() ; ++i ){
                if(u.getFolders().get(1).getEmails().get(i).getId() == e.getId()) {
                    u.getFolders().get(1).getEmails().remove(i);
                    break ;
                }
            }
            u.addEmailToFolder("sent", email) ;
            for (int i = 0; i < email.getRecievers().size(); i++) {
                User r = DataHelper.getUserByAccount(email.getRecievers().get(i));
                if(r != null){
                    Email email2 = e.build();
                    email2.setRead(false);
                    email2.setId(r.getIdMessage());
                    r.addEmailToFolder("inbox", email2);
                    r.setIdMessage(r.getIdMessage()+1);
                }
            }
            d.saveToJson();
            return u.getFolders().get(1).getEmails() ;
        }
        return null ;
    }
    public List<Email> deleteEmail(String account , String folder , int [] id) throws IOException {
        if(folder.equals("inbox"))
            return deleteFromInbox(account, id) ;
        return deleteFromAnyFolder(account, folder, id);
    }
    public List<Email> deleteFromAnyFolder(String account , String folder , int [] id) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(Folder f : u.getFolders()){
                if(f.getName().equals(folder)){
                    for(int i = 0 ; i < id.length ; i++ ){
                        for (int k = 0 ; k < f.getEmails().size() ; k++){
                            if(f.getEmails().get(k).getId() == id[i]){
                                f.getEmails().remove(k) ;
                                break ;
                            }
                        }
                    }
                    d.saveToJson();
                    return f.getEmails() ;
                }
            }

        }
        return null ;

    }
    public List<Email> deleteFromInbox (String account , int [] id) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(int i = 0 ; i < id.length ; i++){
                for (int j = 0 ; j < u.getFolders().get(0).getEmails().size() ; j++){
                    if(u.getFolders().get(0).getEmails().get(j).getId() == id[i] ){
                        u.getFolders().get(3).addEmail(u.getFolders().get(0).getEmails().get(j));
                        u.getFolders().get(0).getEmails().remove(j);
                        break ;
                    }
                }
            }
            d.saveToJson();
            return u.getFolders().get(0).getEmails() ;
        }
        return null ;
    }
    public List<Email> restoreFromTrash (String account , int [] id) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(int i = 0 ; i < id.length ; i++){
                for (int j = 0 ; j < u.getFolders().get(3).getEmails().size() ; j++){
                    if(u.getFolders().get(3).getEmails().get(j).getId() == id[i] ){
                        u.getFolders().get(0).addEmail(u.getFolders().get(3).getEmails().get(j));
                        u.getFolders().get(3).getEmails().remove(j);
                        break ;
                    }
                }
            }
            d.saveToJson();
            return u.getFolders().get(3).getEmails() ;
        }
        return null ;
    }
    public List<Email> editPriority(String account , int id , int priority) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(Email e : u.getFolders().get(0).getEmails()){
                if(e.getId() == id){
                    e.setPriority(priority);
                    d.saveToJson();
                    return u.getFolders().get(0).getEmails() ;
                }
            }
        }
        return null ;
    }
    public ArrayList<Folder> addNewFolder(String account , String folder) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            u.addFolder(folder);
            d.saveToJson();
            return showFolder(account) ;
        }
        return null ;
    }
    public List<Email> moveEmailsToFolder(String account , String folder , int[] id) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(int i = 0 ; i < id.length ; ++i ){
                for(Email e : u.getFolders().get(0).getEmails()){
                    if(e.getId() == id[i]){
                        Email copy = e.clone() ;
                        copy.setId(u.getIdMessage());
                        u.addEmailToFolder(folder , copy);
                        u.setIdMessage(u.getIdMessage() + 1 );
                        break ;
                    }
                }
            }
            d.saveToJson();
            return u.getFolders().get(0).getEmails() ;
        }
        return null ;
    }
    public ArrayList<Folder> deleteFolder(String account , String folder) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(int i = 4 ; i < u.getFolders().size() ; i++){
                if(u.getFolders().get(i).getName().equals(folder)){
                    u.getFolders().remove(i) ;
                    d.saveToJson() ;
                    return showFolder(account) ;
                }
            }
        }
        return null ;
    }
    public ArrayList<Folder> showFolder(String account){
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            ArrayList<Folder> folders = new ArrayList<>() ;
            for(int i = 4 ; i < u.getFolders().size() ; ++i){
                folders.add(u.getFolders().get(i)) ;
            }
            return folders ;
        }
        return null ;
    }
    public ArrayList<Folder> renameFolder(String account , String oldFolder , String newFolder) throws IOException {
        User u = DataHelper.getUserByAccount(account);
        if(u != null){
            for(int i = 4 ; i < u.getFolders().size() ; i++){
                if(u.getFolders().get(i).getName().equals(oldFolder)){
                    u.getFolders().get(i).setName(newFolder); ;
                    d.saveToJson() ;
                    return showFolder(account) ;
                }
            }
        }
        return null ;
    }



    public ArrayList<Email> filter (String account ,String folder , String type ,String criteriaValue){
        User u = DataHelper.getUserByAccount(account) ;
        ArrayList<Email> emails = new ArrayList<>() ;
        for(Folder f : u.getFolders()){
            if(f.getName().equals(folder)){
                for(Email e : f.getEmails()){
                    if(!emails.contains(e))
                        emails.add(e) ;
                }
            }
        }

        CriteriaFactory factory = new CriteriaFactory() ;
        ICriteria criteria = factory.getCriteria(type ,criteriaValue ) ;
        return criteria.meetCriteria(emails) ;
    }
    public ArrayList<Contact> filterContact(String account , String type , String criteriaValue){
        User u = DataHelper.getUserByAccount(account) ;
        ContactFactory factory = new ContactFactory () ;
        ICriteriaContact criteria = factory.getCriteria(type , criteriaValue ) ;
        return criteria.meetCriteria(u.getContacts()) ;
    }

    public ArrayList<Email> search (String account , String type ,String criteriaValue){
        User u = DataHelper.getUserByAccount(account) ;
        ArrayList<Email> emails = new ArrayList<>() ;
        for(Folder f : u.getFolders()){
            for(Email e : f.getEmails()){
                if(!emails.contains(e))
                    emails.add(e) ;
            }
        }
        CriteriaFactory factory = new CriteriaFactory() ;
        ICriteria criteria = factory.getCriteria(type ,criteriaValue ) ;
        return criteria.meetCriteria(emails) ;
    }

    public ArrayList<Email> sortEmail (String account, String type, boolean sortingOrder , String folder){
        User u = DataHelper.getUserByAccount(account) ;
        ArrayList<Email> emails = new ArrayList<>() ;
        for(Folder f : u.getFolders()){
            if(f.getName().equals(folder)) {
                for (Email e : f.getEmails()) {
                    if (!emails.contains(e))
                        emails.add(e);
                }
            }
        }
        SortFactory factory = new SortFactory() ;
        Strategy strategy = factory.getSort(type) ;
        return strategy.doOperation(emails, sortingOrder);

    }
    public List<Email> getEmails(String account , String folder) throws ParseException, IOException {
        User u = DataHelper.getUserByAccount(account) ;
        for(Folder f : u.getFolders()){
            if(f.getName().equals(folder)) {
                if(folder.equals("trash")){
                    for(int i = 0 ; i < u.getFolders().get(3).getEmails().size() ; i++){
                        if(isOutDated(u.getFolders().get(3).getEmails().get(i).getDateTime())){
                            u.getFolders().get(3).getEmails().remove(i) ;
                            --i;
                        }
                    }
                    d.saveToJson();
                }
                return f.getEmails() ;
            }
        }
        return null ;
    }

    public boolean isOutDated (String dateTime) throws ParseException {
        LocalDateTime myDateObj = LocalDateTime.now();
        Date now = Date.from(myDateObj.atZone(ZoneId.systemDefault()).toInstant());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        Date date = formatter.parse(dateTime);

        long milliseconds1 = date.getTime();
        long milliseconds2 = now.getTime();
        long diff = milliseconds2 - milliseconds1;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if(diffDays <= 29)
            return false;
        return true;
    }
}
