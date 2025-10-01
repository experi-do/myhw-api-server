package com.sk.skala.stockapi.controller;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public Response getRanking() {
        Response response = new Response();
        response.setBody(rankingService.getRanking());
        return response;
    }
}
