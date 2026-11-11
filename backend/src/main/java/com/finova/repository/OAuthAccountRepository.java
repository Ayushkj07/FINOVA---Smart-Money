package com.finova.repository;

import com.finova.entity.OAuthAccount;
import com.finova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);
    List<OAuthAccount> findByUser(User user);
    Optional<OAuthAccount> findByUserAndProvider(User user, String provider);
    boolean existsByProviderAndProviderUserId(String provider, String providerUserId);
    void deleteByUserAndProvider(User user, String provider);
}
