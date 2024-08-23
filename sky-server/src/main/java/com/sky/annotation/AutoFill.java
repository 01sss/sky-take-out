package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Tan
 * @createTime: 2024/08/23 10:16
 * @description: 自定义注解：用于标识某个方法需要进行公共字段的自动填充
 */
@Target(ElementType.METHOD) // 个注解用于限定被它修饰的注解类（Annotation Class）只能用于方法上。例如，如果定义了一个注解类，并使用了这个@Target注解限定其目标为方法，那么这个注解类只能用于方法声明上，不能用于类、变量、构造函数等其他元素。
@Retention(RetentionPolicy.RUNTIME) // RetentionPolicy.RUNTIME表示这个注解在运行时仍然有效，即这个注解信息会被编译到class文件中，并且可以通过反射（Reflection）在运行时访问。这样，可以在运行时通过反射机制来检查某个方法、类等是否被特定的注解标记，从而实现一些特定的功能，比如日志记录、权限控制、缓存处理等。
public @interface AutoFill {
    // 数据库操作类型：UPDATE INSERT
    OperationType value();
}
