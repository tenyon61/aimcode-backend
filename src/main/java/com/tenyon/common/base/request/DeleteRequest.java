package com.tenyon.common.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "删除请求参数")
public class DeleteRequest implements Serializable {

    @Schema(description = "id")
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

}