package com.loanflow.mapper;

import com.loanflow.dto.response.UserResponse;
import com.loanflow.entity.user.User;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole())")
    @Mapping(target = "isActive", source = "active")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
