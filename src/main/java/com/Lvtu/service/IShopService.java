package com.Lvtu.service;

import com.Lvtu.dto.Result;
import com.Lvtu.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {
    Result queryById(Long id);

    Result update(Shop shop);
}
