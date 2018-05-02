package com.taotao.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.mapper.ItemMapper;
import com.taotao.manage.mapper.ItemParamItemMapper;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;

@Service
public class ItemService extends BaseService<Item> {
    // Service中可以注入其他Service
    @Autowired
    private ItemDescService itemDescService;

    @Autowired
    private ItemMapper itemMapper;
    
    @Autowired
    private ItemParamItemService itemParamItemService;
    
    /**
     * 新增商品
     * 
     * @param item
     * @param desc
     */
    public void saveItem(Item item, String desc, String itemParams) {
        // 设置初始数据
        item.setStatus(1); // 生效
        item.setId(null); // 强制设置ID为null
        super.save(item);

        // 保存描述数据
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        this.itemDescService.save(itemDesc);
        
        // 保存商品规格参数数据
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setItemId(item.getId());
        itemParamItem.setParamData(itemParams);
        this.itemParamItemService.save(itemParamItem);
    }
    
    /**
     * 修改商品
     * 
     * @param item
     * @param desc
     */
    public void updateItem(Item item, String desc, String itemParams) {
        //将不能修改的数据设置为null
        item.setStatus(null);
        item.setCreated(null);
        //有选择的更新，选择不为null的字段作为插入字段
        super.updateSelective(item);
        
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        this.itemDescService.update(itemDesc);
        
        // 保存商品规格参数数据
        this.itemParamItemService.updateItemParamItem(item.getId(), itemParams);
    }

    public PageInfo<Item> queryPageList(Integer page, Integer rows) {
        Example example = new Example(Item.class);
        example.setOrderByClause("updated DESC");

        // 设置分页参数
        PageHelper.startPage(page, rows);
        List<Item> list = this.itemMapper.selectByExample(example);
        return new PageInfo<Item>(list);
    }

}
