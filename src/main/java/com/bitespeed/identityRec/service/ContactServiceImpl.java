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
import java.util.stream.Collectors;

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

    public void validateRequest(String phoneNumber, String email) {
        if (phoneNumber == null && email == null) {
            throw new IllegalArgumentException("Both phoneNumber and email cannot be null.");
        }
    }
    public ContactResponseObject addContactRestCall(String email, String number) {

        // BOTH CHECKS REQUIRED
        int c_email = contactRepository.findByEmail(email).size();
        int c_number = contactRepository.findByPhoneNumber(number).size();

        // Check if new User based on email and phoneNumber
        if(c_email == 0 && c_number ==0){
            Contact newContact = new Contact();
            newContact.setEmail(email);
            newContact.setPhoneNumber(number);
            newContact.setLinkPrecedence("primary");

            contactRepository.save(newContact);
            return getContactResponse(newContact);
        }

        Contact oldContact = contactRepository.findByEmailAndPhoneNumber(email, number);
        if (oldContact != null) {
            // Contact was found with the same number and phone. No record inserted
            return getContactResponse(oldContact);
        }

        int numberCount = 0;
        int emailCount = 0;
        if (email != null)
            emailCount = contactRepository.findCountByEmail(email);

        if (number != null)
            numberCount = contactRepository.findCountByPhoneNumber(number);

        if (emailCount > 0 && numberCount > 0) {
            //Both email and contact present in different records need to modify accordingly
            // Think of it is as two different chain which may or may not have secondary elements;
            Contact commonPrimary = addContactTwoPrimary(email, number);
            return getContactResponse(commonPrimary);
        }

        if ((emailCount > 0 && number != null) || (numberCount > 0 && email != null)) {
            // query the database to get list of all ids where this email is present this will be all secondary account or a primary account if primary account get the
            // For these types we already have the email/phoneNumber in some account we just need to find the PrimiaryId for new object

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
            Contact newContact = addContact(email, number,primaryId);
            return getContactResponse(newContact);
        }

        else{
            // Last case when a registered email is there with a null phoneNumber in this case we will not register a new row
            Contact contact= contactRepository.findContactbyEmailorNumber(email,number).get(0);
            return getContactResponse(contact);
        }

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
        // we make a choice of using the first created contact as the new primary
        if  (topEmail.getCreatedAt().compareTo(topNumber.getCreatedAt()) >= 0) {
            contactRepository.updateRecordsTwoPrimary("secondary", topEmail.getId(), Objects.equals(topNumber.getLinkPrecedence(), "secondary") ? topNumber.getLinkedId() : topNumber.getId(), topEmail);
        }else {
            contactRepository.updateRecordsTwoPrimary("secondary", topNumber.getId(), Objects.equals(topEmail.getLinkPrecedence(), "secondary") ? topEmail.getLinkedId() : topEmail.getId(), topNumber);
        }
        logger.info("Updated Chain records");

        return (topEmail.getCreatedAt().compareTo(topNumber.getCreatedAt()) >= 0)? topEmail:topNumber;
    }

    public ContactResponseObject getContactResponse(Contact contact){
        if (contact != null) {
            ContactResponseData contactResponseData = new ContactResponseData();
            contactResponseData.setPrimaryContactId((Objects.equals(contact.getLinkPrecedence(), "primary")) ? contact.getId() : contact.getLinkedId() );
            contactResponseData.setEmails(contactRepository.getEmailsByLinkedId(contactResponseData.getPrimaryContactId()).stream().map(Contact::getEmail).distinct().collect(Collectors.toList()));
            contactResponseData.setPhoneNumbers(contactRepository.getPhoneNumbersByLinkedId(contactResponseData.getPrimaryContactId()).stream().map(Contact::getPhoneNumber).distinct().collect(Collectors.toList()));
            contactResponseData.setSecondaryContactIds(contactRepository.getSecondaryIDs(contactResponseData.getPrimaryContactId()).stream().map(Contact::getId).distinct().collect(Collectors.toList()));

            ContactResponseObject contactResponseObject = new ContactResponseObject();
            contactResponseObject.setContact(contactResponseData);
            return contactResponseObject;
        }
        return null;

    }
}
