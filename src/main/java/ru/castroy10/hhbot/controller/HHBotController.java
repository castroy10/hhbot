package ru.castroy10.hhbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import ru.castroy10.hhbot.service.BlockingService;
import ru.castroy10.hhbot.service.ResumeService;
import ru.castroy10.hhbot.service.TokenService;

@Controller
@RequiredArgsConstructor
public class HHBotController {

    private final TokenService tokenService;
    private final ResumeService resumeService;
    private final BlockingService blockingService;

    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/init")
    @ResponseBody
    public String init(@RequestParam("code") final String code) {
        final boolean initToken = tokenService.getInitToken(code);
        return initToken ? "Токен успешно инициирован" : "Не удалось инициировать токен";
    }

    @GetMapping("/getAuthCode")
    public RedirectView getAuthCode() {
        return new RedirectView(tokenService.getAuthUrl());
    }

    @GetMapping("/updateResume")
    @ResponseBody
    public String update() {
        final boolean success = resumeService.updateResume();
        return success ? "Резюме успешно обновлено" : "Не удалось обновить резюме";
    }

    @GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String checkToken() {
        final boolean tokenValid = tokenService.isTokenValid();
        return tokenValid ? "Токен валидный" : "Не удалось проверить токен";
    }

    @GetMapping("/shutdown")
    @ResponseBody
    public String shutdown() {
        blockingService.shutdown();
        return "Сервер остановлен";
    }

}