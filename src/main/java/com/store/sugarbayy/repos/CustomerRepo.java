package com.store.sugarbayy.repos;

import com.store.sugarbayy.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    @Query("""
        select c from Customer c
        where c.email = :identifier or c.contactNumber = :identifier
    """)
    Optional<Customer> findByIdentifier(@Param("identifier") String identifier);

    @Query("select c from Customer c where c.email = :email")
    Optional<Customer> findByEmail(@Param("email") String email);

    @Query("select count(c) > 0 from Customer c where c.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
