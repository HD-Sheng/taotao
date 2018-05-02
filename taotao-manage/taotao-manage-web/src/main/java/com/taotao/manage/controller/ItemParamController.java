package com.taotao.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.ItemParam;
import com.taotao.manage.service.ItemParamService;

@Controller
@RequestMapping(value = "item/param")
public class ItemParamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemParamController.class);

    @Autowired
    private ItemParamService itemParamService;
    
    /**
     * 根据类目ID查找规格参数模板
     * @param itemCatId
     * @return
     */
    @RequestMapping(value = "{itemCatId}", method = RequestMethod.GET)
    public ResponseEntity<ItemParam> queryByItemCatId(@PathVariable(value = "itemCatId") Long itemCatId) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("根据id查询模板！ id = {}", itemCatId);
            }
            ItemParam record = new ItemParam();
            record.setItemCatId(itemCatId);
            ItemParam itemParam = this.itemParamService.queryOne(record);
            if (itemParam == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(itemParam);
        } catch (Exception e) {
            LOGGER.error("查询失败！ id = " + itemCatId + "," + e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    
    /**
     * 新增商品规格
     * 从前台传过来两个数据：1. cid(也就是itemCatId)， 2.paramData(也就是转成json后的规格参数)
     * 那么现在接收有两个方式。
     *      1. <code>save(@RequestParam("paramData") String paramData, @PathVariable(value = "itemCatId") Long itemCatId)</code>
     *      2. <code>save(ItemParam itemParam,                         @PathVariable(value = "itemCatId") Long itemCatId)</code>
     *          2.1. 因为ItemParam中有一个paramData属性，所以可以直接接收。
     * @param itemCatId
     * @param paramData
     * @return
     */
    @RequestMapping(value = "{itemCatId}", method = RequestMethod.POST)
    public ResponseEntity<Void> save(@RequestParam("paramData") String paramData, @PathVariable(value = "itemCatId") Long itemCatId){
        try {
            ItemParam itemParam = new ItemParam();
            itemParam.setItemCatId(itemCatId);
            itemParam.setParamData(paramData);
            this.itemParamService.save(itemParam);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "30") Integer rows ){
        try {
            PageInfo<ItemParam> pageInfo = this.itemParamService.queryPageList(page, rows);
            EasyUIResult easyUIResult = new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
            return ResponseEntity.ok(easyUIResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
