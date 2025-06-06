package com.tenyon.web.domain.dto.user;

import com.tenyon.common.base.constant.AimConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "用户登录请求体")
@Data
public class UserLoginDTO implements Serializable {

    @Size(min = 4, message = "账号不能少于4位")
    @Schema(description = "账号")
    private String userAccount;

    @Pattern(regexp = AimConstant.REGEX_PASSWORD, message = "密码格式错误(8-18位含数字字母)")
    @Schema(description = "密码")
    private String userPassword;

    @Serial
    private static final long serialVersionUID = 1L;

}
