package com.kunlv.ddd.j.enode.core.annotation;

import java.lang.annotation.*;

/**
 * @program: JGEnode
 * @author: LK
 * @create: 2019-09-14 00:53
 **/
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {
}
