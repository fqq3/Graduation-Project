package com.fqq.futurecommunity.community.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Owner */
@Getter
@Setter
@Accessors(chain = true)
@TableName("owner")
public class Owner extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "owner_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;

    @TableField("owner_nickname")
    private String ownerNickname;

    @TableField("owner_real_name")
    private String ownerRealName;

    @TableField("owner_gender")
    private String ownerGender;

    @TableField("owner_age")
    private Integer ownerAge;

    @TableField("owner_id_card")
    private String ownerIdCard;

    @TableField("owner_phone_number")
    private String ownerPhoneNumber;

    @TableField("owner_open_id")
    private String ownerOpenId;

    @TableField("owner_wechat_id")
    private String ownerWechatId;

    @TableField("owner_qq_number")
    private String ownerQqNumber;

    @TableField("owner_birthday")
    private LocalDate ownerBirthday;

    @TableField("owner_portrait")
    private String ownerPortrait;

    @TableField("owner_signature")
    private String ownerSignature;

    @TableField("owner_status")
    private String ownerStatus;

    @TableField("owner_logon_mode")
    private String ownerLogonMode;

    @TableField("owner_type")
    private String ownerType;

    @TableField("owner_password")
    private String ownerPassword;

    @TableField(exist = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
}
