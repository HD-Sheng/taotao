package com.taotao.manage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.manage.pojo.ItemCat;
import com.taotao.manage.service.ItemCatService;

@Controller
@RequestMapping(value = "item/cat")
public class ItemCatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCatController.class);

    @Autowired
    private ItemCatService itemCatService;
    
    /**
     * 根据父节点ID查询子节点
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ItemCat>> queryItemCatListByParentId(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            if(LOGGER.isInfoEnabled()){
                LOGGER.info("parentId = {}", parentId);
            }
            ItemCat record = new ItemCat();
            record.setParentId(parentId);
            List<ItemCat> list = this.itemCatService.queryListByWhere(record);
            if (list == null || list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            LOGGER.error("查询失败！ + " + e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
