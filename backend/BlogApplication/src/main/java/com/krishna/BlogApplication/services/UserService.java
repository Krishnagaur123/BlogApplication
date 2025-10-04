package com.krishna.BlogApplication.services;

import com.krishna.BlogApplication.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
