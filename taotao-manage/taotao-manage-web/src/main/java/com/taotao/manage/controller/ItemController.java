package com.taotao.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.service.ItemService;

@Controller
@RequestMapping("item")
public class ItemController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    /**
     * 新增商品
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> save(Item item, @RequestParam("desc") String desc,
            @RequestParam("itemParams") String itemParams) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("开始新增商品， item = {}, desc = {}", item, desc);
            }
            if (StringUtils.isEmpty(item.getTitle())) {
                // 响应 400
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // 保存商品的基础数据
            this.itemService.saveItem(item, desc, itemParams);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("新增商品成功, itemId = {}", item.getId());
            }

            // 成功 201
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            LOGGER.error("新增商品失败！ title" + item.getTitle() + ", cid = " + item.getCid() + e);
        }
        // 出错 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 修改商品
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> update(Item item, @RequestParam("desc") String desc,
            @RequestParam("itemParams") String itemParams) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("开始修改商品， item = {}, desc = {}", item, desc);
            }
            if (StringUtils.isEmpty(item.getTitle())) {
                // 响应 400
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            this.itemService.updateItem(item, desc, itemParams);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("修改商品成功, itemId = {}", item.getId());
            }

            // 修改成功 204
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            LOGGER.error("修改商品失败！ title = " + item.getTitle() + ", cid = " + item.getCid() + "," + e);
        }
        // 出错 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryItemList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "30") Integer rows) {

        try {
            PageInfo<Item> pageInfo = this.itemService.queryPageList(page, rows);
            EasyUIResult easyUIResult = new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
            return ResponseEntity.ok(easyUIResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出错 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
}
