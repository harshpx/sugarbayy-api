package com.store.sugarbayy.repos;

import com.store.sugarbayy.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {
    @Query("""
        select a from Admin a
        where a.email = :identifier or a.contactNumber = :identifier
    """)
    Optional<Admin> findByIdentifier(@Param("identifier") String identifier);

    @Query("select a.id from Admin a where a.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    @Query("select count(a) > 0 from Admin a where a.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
