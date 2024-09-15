package com.organisation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organisation.entity.CustomUser;

public interface UserInfoRepository extends JpaRepository<CustomUser,Integer> {

}
