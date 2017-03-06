package com.everseeker.service;

import com.everseeker.entity.HouseStock;
import com.everseeker.mapper.HouseStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by everseeker on 2017/3/3.
 */
@Service
public class HouseStockService {
    @Autowired
    private HouseStockMapper houseStockMapper;

    public void addHouseStock(HouseStock houseStock) {
        houseStockMapper.addHouseStock(houseStock);
    }
}
