package com.store.sugarbayy.repos;

import com.store.sugarbayy.entities.VerificationToken;
import com.store.sugarbayy.enums.VerificationTokenType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken, Long> {

    @Query("""
        select count(v) > 0 from VerificationToken v
        where v.expiry > CURRENT_TIMESTAMP
            and v.manualRevoke = false
            and v.identifier = :identifier
            and v.type = :type
    """)
    boolean existsValidToken(
            @Param("identifier") String identifier,
            @Param("type") VerificationTokenType type
    );

    @Query("""
        select v from VerificationToken v
        where v.expiry > CURRENT_TIMESTAMP
            and v.manualRevoke = false
            and v.identifier = :identifier
            and v.type = :type
        order by v.createdAt desc
    """)
    List<VerificationToken> findValidToken(
            @Param("identifier") String identifier,
            @Param("type") VerificationTokenType type,
            Pageable pageable
    );
}
