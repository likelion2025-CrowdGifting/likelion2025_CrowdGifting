package com.likelion.demoday.domain.entity;

import com.likelion.demoday.domain.enums.Bank;
import com.likelion.demoday.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Bank bank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long balance = 0L;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public void addBalance(Long amount) {
        this.balance += amount;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(String email, String passwordHash, String nickname, Bank bank, String accountNumber) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.status = UserStatus.ACTIVE;
    }

    // 닉네임 수정
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 계좌 정보 수정
    public void updateBankAccount(Bank bank, String accountNumber) {
        this.bank = bank;
        this.accountNumber = accountNumber;
    }

    // 회원탈퇴
    public void withdraw() {
        this.status = UserStatus.DELETED;
    }
}

