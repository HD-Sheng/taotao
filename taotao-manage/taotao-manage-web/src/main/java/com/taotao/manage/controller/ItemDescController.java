package com.taotao.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.service.ItemDescService;

@Controller
@RequestMapping("item/desc")
public class ItemDescController {
    
    @Autowired
    private ItemDescService itemDescService;
    
    /**
     * 根据id查询商品描述信息
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ResponseEntity<ItemDesc> queryItemDesc(@PathVariable(value = "itemId") Long id){
        try {
            ItemDesc itemDesc = this.itemDescService.queryById(id);
            if(itemDesc == null){
                //资源找不到，404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            //成功,200
            return ResponseEntity.ok(itemDesc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //出错，500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
