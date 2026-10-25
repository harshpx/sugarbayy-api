package com.store.sugarbayy.repos;

import com.store.sugarbayy.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    @Modifying
    @Query("delete from RefreshToken rt where rt.admin.id = :id or rt.customer.id = :id")
    void deleteByAdminOrCustomerId(@Param("id") Long id);
}
