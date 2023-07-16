package com.bitespeed.identityRec.DAO;

import com.bitespeed.identityRec.entity.Contact;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ContactRepository extends JpaRepository<Contact, Integer> {


    // BOth check if combination exist in same record no need to add
    @Query("SELECT c FROM Contact c WHERE c.email = :email AND c.phoneNumber = :phoneNumber")
    Contact findByEmailAndPhoneNumber(String email, String phoneNumber);

    // BOth check if combination exist in same record no need to add
    @Query("SELECT count(*)  FROM Contact c WHERE c.email = :email")
    int findCountByEmail(String email);

    @Query("SELECT count(*) FROM Contact c WHERE c.phoneNumber = :phoneNumber")
    int findCountByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Contact c WHERE c.phoneNumber = :phoneNumber order by c.createdAt asc")
    List<Contact> findByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Contact c WHERE c.email = :email order by c.createdAt asc")
    List<Contact> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Contact c SET c.linkPrecedence = :linkPrecedence, c.linkedId = :excludedContactId WHERE c <> :excludedContact AND (c.linkedId = :rId1 OR c.linkedId = :rId2 OR c.id = :rId2)")
    void updateRecordsTwoPrimary(@Param("linkPrecedence") String linkPrecedence, @Param("excludedContactId") Integer excludedContactId, @Param("rId1") Integer rId1, @Param("rId2") Integer rId2, @Param("excludedContact") Contact excludedContact);

    @Query("SELECT DISTINCT c.email FROM Contact c WHERE c.linkedId = :linkedId or c.id = :linkedId")
    List<String> getEmailsByLinkedId(Integer linkedId);

    @Query("SELECT DISTINCT c.phoneNumber FROM Contact c WHERE c.linkedId = :linkedId or c.id = :linkedId")
    List<String> getPhoneNumbersByLinkedId(Integer linkedId);

    @Query("SELECT c.id FROM Contact c WHERE c.linkedId = :linkedId")
    List<Long> geIdByLinkedID(Long linkedId);

    @Query("(SELECT c.id FROM Contact c WHERE c.email = :email)")
    Integer findPrimaryId(String email);

    @Query("(SELECT c.id FROM Contact c WHERE c.email = :email and c.linkPrecedence = 'primary')")
    Integer findPrimaryIdfromPrimaryIdsEmail(String email);

    @Query("(SELECT DISTINCT c.linkedId FROM Contact c WHERE c.email = :email and c.linkPrecedence = 'secondary')")
    Integer findPrimaryIdfromSecondaryIdsEmail(String email);

    @Query("(SELECT c.id FROM Contact c WHERE c.phoneNumber = :number and c.linkPrecedence = 'primary')")
    Integer findPrimaryIdfromPrimaryIdsNumber(String number);
    @Query("(SELECT DISTINCT c.linkedId FROM Contact c WHERE c.phoneNumber = :number and c.linkPrecedence = 'secondary')")
    Integer findPrimaryIdfromSecondaryIdsNumber(String number);

    @Query("(SELECT c FROM Contact c WHERE c.phoneNumber = :number or c.email = :email)")
    List<Contact> findContactbyEmailorNumber(String email,String number);

    @Query("(SELECT c.id FROM Contact c WHERE c.linkedId = :linkedId)")
    List<Integer> getSecondaryIDs(Integer linkedId);
}
