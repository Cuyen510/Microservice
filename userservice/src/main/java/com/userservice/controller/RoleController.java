package com.userservice.controller;

import com.userservice.model.Role;
import com.userservice.service.RoleService.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping()
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.ok().body(roleService.getAllRoles());
    }
}
