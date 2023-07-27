package com.bitespeed.identityRec.web;

import com.bitespeed.identityRec.Model.ContactResponseObject;
import com.bitespeed.identityRec.Model.UserRequestObject;
import com.bitespeed.identityRec.service.ContactService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RequestMapping("/identity")
@RestController
public class CustomerRestController {

    public ContactService contactService;

    public CustomerRestController(ContactService contactService){
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponseObject> processIdentity(@RequestBody UserRequestObject userRequestObject) {
        // Let service validate null case
        contactService.validateRequest(userRequestObject.getEmail(), userRequestObject.getPhoneNumber());
        // Get response Object
        ContactResponseObject responseDTO = contactService.addContactRestCall(userRequestObject.getEmail(), userRequestObject.getPhoneNumber());
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping
    public String redirectToPost(){
        return "This application only takes POST Request";
    }


}
