package com.tenyon.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户和角色关联表
 *
 * @TableName user_role
 */
@TableName(value = "user_role")
@Data
public class UserRole implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}