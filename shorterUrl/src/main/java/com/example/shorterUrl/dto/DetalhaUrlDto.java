package com.example.shorterUrl.dto;

import com.example.shorterUrl.model.ShorterUrl;

public record DetalhaUrlDto(Long id, String shortUrl, String longUrl){

    public DetalhaUrlDto(ShorterUrl shorterUrl){
        this(shorterUrl.getId(), shorterUrl.getShortUrl(), shorterUrl.getLongUrl());
    }

}
