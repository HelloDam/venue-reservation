package com.vrs.init;

import com.vrs.service.VenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2025/1/28 9:59
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class VenueLocationCacheInit implements CommandLineRunner {

    private final VenueService venueService;

    @Override
    public void run(String... args) throws Exception {
        log.info("读取数据库中的场馆信息，将其位置存储缓存到Redis");
        venueService.cacheVenueLocations();
        log.info("场馆位置缓存成功");
    }
}
