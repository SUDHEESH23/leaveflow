package com.example.leaveflow.service;

import com.example.leaveflow.dto.RegisterRequest;
import com.example.leaveflow.dto.RegisterResponse;
import com.example.leaveflow.entity.UserAccount;
import com.example.leaveflow.entity.UserRole;
import com.example.leaveflow.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder) {

        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        String passwordHash = passwordEncoder.encode(request.getPassword());
        UserAccount userAccount = new UserAccount(
                request.getEmail(),
                passwordHash,
                UserRole.EMPLOYEE
        );
        UserAccount savedAccount =  userAccountRepository.save(userAccount);
        return new RegisterResponse(
                savedAccount.getId(),
                savedAccount.getEmail(),
                savedAccount.getRole()
        );
    }
}
