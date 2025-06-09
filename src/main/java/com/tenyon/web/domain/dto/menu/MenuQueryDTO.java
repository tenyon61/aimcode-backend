package com.tenyon.web.domain.dto.menu;

import com.tenyon.common.base.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单查询请求体")
@Data
public class MenuQueryDTO extends PageRequest implements Serializable {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "权限标识")
    private String perms;

    @Serial
    private static final long serialVersionUID = 1L;

}