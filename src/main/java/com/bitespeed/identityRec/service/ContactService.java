package com.bitespeed.identityRec.service;

import com.bitespeed.identityRec.Model.ContactResponseObject;
import com.bitespeed.identityRec.entity.Contact;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactService {
    ContactResponseObject addContactRestCall(String email, String number);
    Contact getContact(String email, String number);

    void validateRequest(String phoneNumber, String email);
}
