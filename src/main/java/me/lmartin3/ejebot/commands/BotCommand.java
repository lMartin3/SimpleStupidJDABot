package me.lmartin3.ejebot.commands;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BotCommand {
    String name() default "";
    String prefix() default "g.";
    String description() default "";
    boolean botUsable() default false;
    boolean onlyCreators() default false;
    Permission permission() default Permission.MESSAGE_WRITE;
}
