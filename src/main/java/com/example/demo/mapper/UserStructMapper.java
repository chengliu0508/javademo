package com.example.demo.mapper;

import com.example.demo.entity.UserEntity;
import com.example.demo.vo.LoginResponseVO;
import com.example.demo.vo.MeResponseVO;
import com.example.demo.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStructMapper {
    UserVO toUserVO(UserEntity user);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "userId", source = "user.id")
    LoginResponseVO toLoginResponse(UserEntity user, String token);

    @Mapping(target = "userId", source = "id")
    MeResponseVO toMeResponse(UserEntity user);
}

