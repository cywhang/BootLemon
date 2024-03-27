package com.blue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


// 실행중인 프로젝트의 Profile이 뭔지 확인할 수 있는 API
@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final Environment env;

    @GetMapping("/nginxprofile")
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());  // 현재 실행 중인 ActiveProfile을 모두 가져온다
        List<String> realProfiles = Arrays.asList("real", "real1", "real2");
        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

        return profiles.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}