package com.bitespeed.identityRec.web;

import com.bitespeed.identityRec.DAO.ContactRepository;
import com.bitespeed.identityRec.Model.ContactResponseObject;
import com.bitespeed.identityRec.entity.Contact;
import com.bitespeed.identityRec.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/identity")
@RestController
public class CustomerRestController {

    public ContactService contactService;

    public CustomerRestController(ContactService contactService){
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponseObject> processIdentity(@RequestParam(required = false) String email,@RequestParam(required = false) String phoneNumber) {
            // Process the email and phoneNumber fields accordingly
        System.out.println(email + phoneNumber);
            if (email == null && phoneNumber == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Create the ContactResponseDTO and set the necessary fields
            ContactResponseObject responseDTO = contactService.addContactRestCall(email, phoneNumber);
            // Set the fields in the responseDTO as needed
        System.out.println("Here2");
            return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public String hello(){
        return "Hello";
    }

}
