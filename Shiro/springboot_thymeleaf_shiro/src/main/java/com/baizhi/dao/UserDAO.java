package com.baizhi.dao;


import com.baizhi.entity.Perms;
import com.baizhi.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDAO {

    void save(User user);

    User findByUserName(String username);


    //根据用户名查询所有角色
    User findRolesByUserName(String username);

    //根据角色id查询权限集合
    List<Perms> findPermsByRoleId(String id);
}

