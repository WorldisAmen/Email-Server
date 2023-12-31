package com.example.mailServer.mail.services.FilterContact;

import com.example.mailServer.mail.services.Contact;

import java.util.ArrayList;

public class OrCriteria implements ICriteriaContact{
    private ICriteriaContact firstCriteria;
    private ICriteriaContact secondCriteria;


    public OrCriteria(ICriteriaContact firstCriteria, ICriteriaContact secondCriteria) {
        this.firstCriteria = firstCriteria;
        this.secondCriteria = secondCriteria;
    }

    @Override
    public ArrayList<Contact> meetCriteria(ArrayList<Contact> contacts) {

        ArrayList<Contact> firstCriteriaItems = this.firstCriteria.meetCriteria(contacts);
        ArrayList<Contact> secondCriteriaItems = this.secondCriteria.meetCriteria(contacts);

        for (Contact contact : secondCriteriaItems) {
            if(!firstCriteriaItems.contains(contact)) {
                firstCriteriaItems.add(contact);
            }
        }

        return firstCriteriaItems;
    }
}
