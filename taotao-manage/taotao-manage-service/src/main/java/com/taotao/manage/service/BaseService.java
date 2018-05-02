package com.taotao.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

public abstract class BaseService<T extends BasePojo> {

//    public abstract Mapper<T> getMapper();
    
    /**
     * 在Spring4中新增了泛型注入的特性
     * 是根据泛型的类型来动态的找到对应的注入类来注入，也就是说实质内容没有改变，只是书写方式改变了。
     */
    @Autowired
    private Mapper<T> mapper;

    /**
     * 根据ID查询数据
     * 
     * @param id
     * @return
     */
    public T queryById(Long id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    /**
     * 查询所有数据
     * 
     * @return
     */
    public List<T> queryAll() {
        return this.mapper.select(null);
    }

    /**
     * 根据条件查询一条数据
     * 
     * @param record
     * @return
     */
    public T queryOne(T record) {
        return this.mapper.selectOne(record);
    }

    /**
     * 根据条件查询数据列表
     * 
     * @param record
     * @return
     */
    public List<T> queryListByWhere(T record) {
        return this.mapper.select(record);
    }

    /**
     * 分页查询数据列表
     * 
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public PageInfo<T> queryPageListByWhere(Integer pageNum, Integer pageSize, T record) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = this.mapper.select(record);
        return new PageInfo<T>(list);
    }

    /**
     * 新增数据
     * 
     * @param record
     * @return
     */
    public Integer save(T record) {
        record.setCreated(new Date());
        record.setUpdated(record.getCreated());
        return this.mapper.insert(record);
    }

    /**
     * 有选择的保存，选择不为null的字段作为插入字段
     * 
     * @param record
     * @return
     */
    public Integer saveSelective(T record) {
        record.setCreated(new Date());
        record.setUpdated(record.getCreated());
        return this.mapper.insertSelective(record);
    }

    /**
     * 更新数据
     * 
     * @param record
     * @return
     */
    public Integer update(T record) {
        record.setUpdated(new Date());
        return this.mapper.updateByPrimaryKey(record);
    }

    /**
     * 有选择的更新，选择不为null的字段作为插入字段
     * 
     * @param record
     * @return
     */
    public Integer updateSelective(T record) {
        record.setUpdated(new Date());
        return this.mapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 根据ID删除数据
     * 
     * @param id
     * @return
     */
    public Integer deleteById(Long id) {
        return this.mapper.deleteByPrimaryKey(id);
    }

    /**
     * 批量删除
     * 
     * @param clazz
     * @param property
     * @param values
     * @return
     */
    public Integer deleteByIds(Class<T> clazz, String property, List<Object> values) {
        Example example = new Example(clazz);
        example.createCriteria().andIn(property, values);
        return this.mapper.deleteByExample(example);
    }

    /**
     * 根据条件删除数据
     * 
     * @param record
     * @return
     */
    public Integer deleteByWhere(T record) {
        return this.mapper.delete(record);
    }
}
