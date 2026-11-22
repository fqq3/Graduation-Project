package com.fqq.futurecommunity.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 23:45
 * @Description: com.fqq.futurecommunity.system.domain
 * @version: 1.0
 */
public class SysArea implements Serializable{
        private static final long serialVersionUID=1L;

        @TableId(value = "id", type = IdType.AUTO)
        private Integer id;
        /**
         * 区划码
         */
        private Integer code;
        /**
         * 区划名称
         */
        private String name;
        /**
         * 上级区划码
         */
        private Integer parentCode;

        public Integer getId() {return id;}

        public void setId(Integer id) {this.id = id;}

        public Integer getCode() {
                return code;
            }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getParentCode() {
            return parentCode;
        }

        public void setParentCode(Integer parentCode) {
            this.parentCode = parentCode;
        }

}
