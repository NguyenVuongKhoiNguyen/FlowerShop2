package com.poly.models.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.models.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
	
	Optional<Account> findByEmail(String email); //if not found then return Optional.empty()
	
    @Query(value = """
    EXEC sp_FilteredPaginatedAccounts
        @Username = :username,
        @Fullname = :fullname,
        @Email = :email,
        @Activated = :activated,
        @SortOrder = :sortOrder,
        @Page = :page,
        @PageSize = :pageSize
    """, nativeQuery = true)
    List<Account> filteredPaginatedAccounts(
            @Param("username") String username,
            @Param("fullname") String fullname,
            @Param("email") String email,
            @Param("activated") Boolean activated,
            @Param("sortOrder") String sortOrder,
            int page,
            int pageSize
    );

    // COUNT
    @Query(value = """
        EXEC sp_CountFilteredAccounts
            @Username = :username,
            @Fullname = :fullname,
            @Email = :email,
            @Activated = :activated
    """, nativeQuery = true)
    Long countFilteredAccounts(
    		@Param("username") String username,
    		@Param("fullname") String fullname,
    		@Param("email") String email,
    		@Param("activated") Boolean activated
    );
}
