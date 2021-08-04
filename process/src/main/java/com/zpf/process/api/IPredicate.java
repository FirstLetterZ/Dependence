package com.zpf.process.api;

/**
 * @author Created by ZPF on 2021/4/21.
 * @see java.util.function.Predicate
 */
public interface IPredicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(T t);
}
