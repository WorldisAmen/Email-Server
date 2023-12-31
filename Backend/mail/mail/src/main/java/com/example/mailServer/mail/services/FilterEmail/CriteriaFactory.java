package com.example.mailServer.mail.services.FilterEmail;

import java.util.ArrayList;

public class CriteriaFactory {
    public ICriteria getCriteria(String criteriaName, String criteriaValue){
        if (criteriaName == null)
            return null;
        else if (criteriaName.equalsIgnoreCase("DATETIME"))
            return new CriteriaDateTime(criteriaValue);
        else if (criteriaName.equalsIgnoreCase("PRIORITY"))
            return new CriteriaPriority(Integer.parseInt(criteriaValue));
        else if (criteriaName.equalsIgnoreCase("RECIEVER"))
            return new CriteriaReciever(criteriaValue);
        else if (criteriaName.equalsIgnoreCase("SENDER"))
            return new CriteriaSender(criteriaValue);
        else if (criteriaName.equalsIgnoreCase("SUBJECT"))
            return new CriteriaSubject(criteriaValue);
        else if (criteriaName.equalsIgnoreCase("BODY"))
            return new CriteriaBody(criteriaValue);
        else if (criteriaName.equalsIgnoreCase("ALL"))
            return new OrCriteria(new CriteriaBody(criteriaValue), new CriteriaSubject(criteriaValue),
                                  new CriteriaReciever(criteriaValue), new CriteriaDateTime(criteriaValue));
        else
            return null;
    }
}
