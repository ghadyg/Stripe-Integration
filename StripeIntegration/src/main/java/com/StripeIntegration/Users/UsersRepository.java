package com.StripeIntegration.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity,String> {
    @Query("SELECT u FROM UsersEntity u WHERE u.verificationCode = ?1")
    public UsersEntity findByVerificationCode(String code);

    Optional<UsersEntity> findByStripeIDLike(String stripeID);

}
