package com.fqq.community;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

/**
 * @author : fqq
 * @since : 2026/3/6 - 03 - 06 - 21:27
 * @Description: com.fqq.community
 * @version: 1.0
 */
public class generator {
    /**
     * MyBatis-Plus 代码生成器
     * 用于根据数据库表结构自动生成 Entity、Mapper、Service、Controller 等代码
     */

        public static void main(String[] args) {

            // 创建代码生成器，并配置数据库连接信息（URL、用户名、密码）
            FastAutoGenerator.create(
                            // JDBC 连接地址：连接本地 MySQL 数据库，数据库名为 community
                            // useUnicode=true：启用 Unicode 编码
                            // characterEncoding=utf8：使用 UTF-8 编码
                            // serverTimezone=Asia/Shanghai：设置时区为亚洲/上海
                            "jdbc:mysql://127.0.0.1:3306/community_manager?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai",
                            "root",    // 数据库登录用户名
                            "root"   // 数据库登录密码
                    )

                    // ==================== 全局配置 ====================
                    .globalConfig(builder -> {
                        builder
                                .author("fqq")  // 设置代码作者，会出现在生成的文件注释中
                                .outputDir("D://Code/CommunityManager/community/src/main/java") // 设置生成代码的输出目录（绝对路径）
                                .dateType(DateType.TIME_PACK)//时间策略
                                .commentDate("yyyy年MM月dd日");//格式化时间格式
                    })

                    // ==================== 包配置 ====================
                    .packageConfig(builder -> {
                        builder
                                .parent("com.fqq.community")  // 设置父包名，所有生成的代码都在此包下
                                .mapper("mapper")              // 设置 Mapper 接口存放的子包名
                                .xml("mapper.xml")              //数据访问层xml包名
                                .entity("entity")              // 设置实体类存放的子包名
                                .service("service")            // 设置 Service 接口存放的子包名
                                .serviceImpl("service.impl")//service实现类包名
                                .controller("controller");     // 设置 Controller 存放的子包名
                    })

                    // ==================== 策略配置 ====================
                    .strategyConfig(builder -> {
                        builder.addInclude("community")  ;         // 指定需要生成代码的数据库表名（可填多个，如 "user","order"）
//                                .mapperBuilder()               // 开始配置 Mapper 生成策略
//                                .enableMapperAnnotation()      // 为 Mapper 接口添加 @Mapper 注解（Spring Boot 可识别）
//                                .enableBaseResultMap()         // 在 XML 中生成通用的 <resultMap> 配置
//                                .enableBaseColumnList();      // 在 XML 中生成通用的 <sql> 列名列表

                        builder.entityBuilder() //实体策略配置
                                //.disableSerialVersionUID()//禁用生成SerialVersionUID：默认true
                                .enableChainModel()//开启链式模型
                                .enableLombok()//开启lombok
                                .enableRemoveIsPrefix()//开启 Boolean 类型字段移除 is 前缀
                                .enableTableFieldAnnotation()//开启生成实体时生成字段注解
                                //.addTableFills()添加表字段填充
                                .naming(NamingStrategy.underline_to_camel)//数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                                .columnNaming(NamingStrategy.underline_to_camel)//表字段映射实体属性命名规则：默认null，不指定按照naming执行
                                .idType(IdType.AUTO)//添加全局主键类型
                                .formatFileName("%s");//格式化实体名称，%s取消首字母I


                        builder.mapperBuilder()//mapper文件策略
                                .enableMapperAnnotation()//开启mapper注解
                                .enableBaseResultMap()//启用xml文件中的BaseResultMap 生成
                                .enableBaseColumnList()//启用xml文件中的BaseColumnList
                                //.cache(缓存类.class)设置缓存实现类
                                .formatMapperFileName("%sMapper")//格式化Dao类名称
                                .formatXmlFileName("%sMapper");//格式化xml文件名称


                        builder.serviceBuilder()//service文件策略
                                .formatServiceFileName("%sService")//格式化 service 接口文件名称
                                .formatServiceImplFileName("%sServiceImpl");//格式化 service 接口文件名称


                        builder.controllerBuilder()//控制层策略
                                //.enableHyphenStyle()开启驼峰转连字符，默认：false
                                .enableRestStyle()//开启生成@RestController
                                .formatFileName("%sController");//格式化文件名称

                    })

                    // 设置使用 Velocity 模板引擎来生成代码文件
//                    .templateEngine(new VelocityTemplateEngine())
                    // 使用 FreeMarker 模板引擎
                    .templateEngine(new FreemarkerTemplateEngine())

                    // 执行代码生成操作
                    .execute();
        }

}
