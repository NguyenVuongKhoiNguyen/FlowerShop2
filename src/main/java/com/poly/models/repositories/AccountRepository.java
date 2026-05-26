package com.poly.models.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
	
	Optional<Account> findByEmail(String email); 
		
	@Query(value = """
        SELECT Username, Password, Fullname, Email, Photo, Address, Phone, CreateDate, Activated
        FROM Accounts
        WHERE
            (:username IS NULL OR Username LIKE '%' + :username + '%')
            AND (:fullname IS NULL OR Fullname LIKE '%' + :fullname + '%')
            AND (:email IS NULL OR Email LIKE '%' + :email + '%')
            AND (:activated IS NULL OR Activated = :activated)
        """,
        countQuery = """
        SELECT COUNT(*) FROM Accounts
        WHERE
            (:username IS NULL OR Username LIKE '%' + :username + '%')
            AND (:fullname IS NULL OR Fullname LIKE '%' + :fullname + '%')
            AND (:email IS NULL OR Email LIKE '%' + :email + '%')
            AND (:activated IS NULL OR Activated = :activated)
        """,
        nativeQuery = true)
    Page<Account> filterAccounts(
            @Param("username")  String username,
            @Param("fullname")  String fullname,
            @Param("email")     String email,
            @Param("activated") Boolean activated,
            Pageable pageable
    );
}
