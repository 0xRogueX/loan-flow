package com.loanflow.mapper;

import com.loanflow.dto.response.UserResponse;
import com.loanflow.entity.user.User;
import org.mapstruct.*;
import java.util.List;

/**
 * Maps the abstract User hierarchy to UserResponse.
 * Works for Borrower, LoanOfficer, and Admin — no instanceof checks needed.
 * getRole() is abstract on User and returns the correct Role from each subclass.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role",     expression = "java(user.getRole())")
    @Mapping(target = "isActive", source = "active")
    UserResponse toResponse(User user);

    /** Batch mapping — used by AdminController.getAllUsers() */
    List<UserResponse> toResponseList(List<User> users);
}
