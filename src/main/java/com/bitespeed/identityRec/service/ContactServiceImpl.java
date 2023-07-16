package com.bitespeed.identityRec.service;

import com.bitespeed.identityRec.DAO.ContactRepository;
import com.bitespeed.identityRec.Model.ContactResponseData;
import com.bitespeed.identityRec.Model.ContactResponseObject;
import com.bitespeed.identityRec.entity.Contact;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class ContactServiceImpl implements ContactService {

    public ContactRepository contactRepository;
    private static final Logger logger = Logger.getLogger(String.valueOf(ContactServiceImpl.class));

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact getContact(String email, String number) {
        Contact foundContacts = contactRepository.findByEmailAndPhoneNumber(email, number);

        return foundContacts;

    }
    public ContactResponseObject addContactRestCall(String email, String number) {
        if (email == null && number == null) {
            throw new RuntimeException("Invalid Request");
        }

        // BOTH CHECKS REQUIRED
        int c_email = contactRepository.findByEmail(email).size();
        int c_number = contactRepository.findByPhoneNumber(number).size();

        if(c_email == 0 && c_number ==0){
            //first time add contact;
            Contact newContact = new Contact();
            newContact.setEmail(email);
            newContact.setPhoneNumber(number);
            newContact.setLinkPrecedence("primary");

            contactRepository.save(newContact);
            return getContactResponse(newContact);

        }
        Contact c = contactRepository.findByEmailAndPhoneNumber(email, number);
        if (c != null) {
            logger.info("Contact was found with the same number and phone. No record inserted");

            return getContactResponse(c);
        }
        int numberCount = 0;
        int emailCount = 0;
        if (email != null) {
            emailCount = contactRepository.findCountByEmail(email);
        }
        if (number != null) {
            numberCount = contactRepository.findCountByPhoneNumber(number);
        }

        if (emailCount > 0 && numberCount > 0) {
            logger.info("Both email and contact present in different records need to modify accordingly");
            // think of it is as two different chain which may or may not have secondary elements;
            c = addContactTwoPrimary(email, number);

            return getContactResponse(c);

        }
        if ((emailCount > 0 && number != null) || (numberCount > 0 && email != null)) {
            //Query for primary account
            // query the database to get list of all ids where this email is present this will be all secondary account or a primary account if primary account get the
            Integer primaryId = null;
            if(emailCount >0){
            primaryId = contactRepository.findPrimaryIdfromPrimaryIdsEmail(email);
            if(primaryId == null){
                logger.info("No primary id found Querying for secondary ids");
                primaryId = contactRepository.findPrimaryIdfromSecondaryIdsEmail(email);
            }
            }
            if(numberCount >0){
                primaryId = contactRepository.findPrimaryIdfromPrimaryIdsNumber(number);
                if(primaryId == null){
                    logger.info("No primary id found Querying for secondary ids");
                    primaryId = contactRepository.findPrimaryIdfromSecondaryIdsNumber(number);
                }
            }
            if(primaryId == null){
                logger.warning("Primary ID not found for number please check");
            }
            c = addContact(email, number,primaryId);
            return getContactResponse(c);
        }

        else{
            Contact contact= contactRepository.findContactbyEmailorNumber(email,number).get(0);
            getContactResponse(contact);
        }

        return new ContactResponseObject();

    }

    public Contact addContact(String email, String number,Integer primaryId){
        Contact newContact = new Contact();
        newContact.setEmail(email);
        newContact.setPhoneNumber(number);
        newContact.setLinkedId(primaryId);
        newContact.setLinkPrecedence("secondary");

        Contact newContactDb =contactRepository.save(newContact);

        return newContactDb;
    }


    public Contact addContactTwoPrimary(String email, String number) {
        List<Contact> allEmail = contactRepository.findByEmail(email);
        List<Contact> allNumbers = contactRepository.findByPhoneNumber(number);

        Contact topEmail = allEmail.get(0);
        Contact topNumber = allNumbers.get(0);
        Contact topContact = null;
        if (topEmail.getCreatedAt().compareTo(topNumber.getCreatedAt()) < 0)
            contactRepository.updateRecordsTwoPrimary("secondary",topEmail.getId(),topEmail.getId(),topNumber .getId(),topEmail);
        else
            contactRepository.updateRecordsTwoPrimary("secondary",topNumber.getId() ,topNumber.getId(),topEmail.getId(),topNumber);

        logger.info("Updated two Chain records");

        return (topEmail.getCreatedAt().compareTo(topNumber.getCreatedAt()) < 0)? topEmail:topNumber;
    }

    public ContactResponseObject getContactResponse(Contact contact){
        if (contact != null) {
            ContactResponseData contactResponseData = new ContactResponseData();
            contactResponseData.setPrimaryContactId((Objects.equals(contact.getLinkPrecedence(), "primary")) ? contact.getId() : contact.getLinkedId() );
            contactResponseData.setEmails(contactRepository.getEmailsByLinkedId(contact.getId()));
            contactResponseData.setPhoneNumbers(contactRepository.getPhoneNumbersByLinkedId(contactResponseData.getPrimaryContactId()));
            contactResponseData.setSecondaryContactIds(contactRepository.getSecondaryIDs(contact.getLinkedId()));
            ContactResponseObject contactResponseObject = new ContactResponseObject();
            contactResponseObject.setContact(contactResponseData);
            return contactResponseObject;
        }
        return null;

    }
}
