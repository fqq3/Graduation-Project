package com.fqq.futurecommunity.system.domain.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 23:46
 * @Description: com.fqq.futurecommunity.system.domain.dto
 * @version: 1.0
 */
public class AreaDto implements Serializable{
   //区域层级模型 - 树结构

        //区划码
        private Integer code;

        //区划名称
        private String name;

        //子区划
        private List<AreaDto> children;

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

        public List<AreaDto> getChildren() {
            return children;
        }

        public void setChildren(List<AreaDto> children) {
            this.children = children;
        }


}
