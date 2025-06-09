package com.tenyon.web.domain.dto.role;

import com.tenyon.common.base.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询请求体")
@Data
public class RoleQueryDTO extends PageRequest implements Serializable {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Serial
    private static final long serialVersionUID = 1L;
}
