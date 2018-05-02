package cn.itcast.usermanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.itcast.usermanager.bean.EasyUIResult;
import cn.itcast.usermanager.mapper.UserMapper;
import cn.itcast.usermanager.pojo.User;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public EasyUIResult queryUserList(Integer page, Integer rows) {
        // 设置分页参数
        PageHelper.startPage(page, rows);
        // 查询User数据
        Example example = new Example(User.class);
        // 设置排序条件
        example.setOrderByClause("UPDATED DESC");
        List<User> users = this.userMapper.selectByExample(example);
        // 获取分页后的信息
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据用户ID查询用户信息
     * 
     * @param id
     * @return
     */
    public User queryUserById(Long id) {
        return this.userMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增用户
     * 
     * @param user
     * @return
     */
    public Boolean saveUser(User user) {
        return this.userMapper.insert(user) == 1;
    }

    public Boolean updateUser(User user) {
        return this.userMapper.updateByPrimaryKeySelective(user) == 1;
    }

    public Boolean deleteUser(Long id) {
        return this.userMapper.deleteByPrimaryKey(id) == 1;
    }
}
