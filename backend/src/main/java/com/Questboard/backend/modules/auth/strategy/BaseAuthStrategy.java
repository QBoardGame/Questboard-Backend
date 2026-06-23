package com.Questboard.backend.modules.auth.strategy;

import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.services.TokenService;

public abstract class BaseAuthStrategy implements AuthStrategy{
    
    protected final TokenService tokenService;
    protected final UserRepository userRepository;

    protected BaseAuthStrategy(TokenService tokenSerciService,
            UserRepository userRepository
    ){
        this.tokenService = tokenSerciService;
        this.userRepository = userRepository;
    }

}
