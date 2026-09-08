package ru.vsu.zhertvydedlina.aiservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.zhertvydedlina.aiservice.user.entity.User;
import ru.vsu.zhertvydedlina.aiservice.user.response.UserResponseDto;
import ru.vsu.zhertvydedlina.aiservice.user.service.UserService;

@Controller
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserResponseDto.from(userService.getUser(user.getId())));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserResponseDto> updateUser(@AuthenticationPrincipal User tokenUser, @RequestBody User newUser) {
        newUser.setId(tokenUser.getId());

        return ResponseEntity.ok(UserResponseDto.from(userService.updateUser(newUser)));
    }

    @DeleteMapping("/profile/delete")
    public ResponseEntity<UserResponseDto> deleteUser(@AuthenticationPrincipal User tokenUser) {
        return ResponseEntity.ok(UserResponseDto.from(userService.deleteUser(tokenUser.getId())));
    }
}
