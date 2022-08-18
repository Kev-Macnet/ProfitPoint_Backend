package tw.com.leadtek.nhiwidget.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import tw.com.leadtek.nhiwidget.constant.LogType;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogDefender {
	String name() default "";

	@AliasFor("value")
	LogType[] logTypes() default {};
	
	LogType[] value() default {};

}
