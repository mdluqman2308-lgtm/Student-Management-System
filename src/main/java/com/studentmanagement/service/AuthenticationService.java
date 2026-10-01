package com.studentmanagement.service;

import com.studentmanagement.dao.AdminDAO;
import com.studentmanagement.model.Admin;
import com.studentmanagement.util.PasswordUtil;
import com.studentmanagement.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;

/** Handles administrator login. */
public class AuthenticationService {

    private final AdminDAO adminDAO = new AdminDAO();

    /** @return the authenticated admin */
    public Admin login(String username, String password) throws ServiceException {
        if (ValidationUtil.isBlank(username)) {
            throw new ServiceException("Username is required.");
        }
        if (ValidationUtil.isBlank(password)) {
            throw new ServiceException("Password is required.");
        }
        try {
            Optional<Admin> found = adminDAO.findByUsername(username.trim());
            if (found.isEmpty() || !PasswordUtil.matches(password, found.get().getSalt(),
                    found.get().getPasswordHash())) {
                throw new ServiceException("Incorrect username or password.");
            }
            return found.get();
        } catch (SQLException e) {
            throw DatabaseErrors.translate(e);
        }
    }
}
