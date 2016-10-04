package com.sun.sylvanas.effective.generic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sylvanasp on 2016/10/4.
 */
public class Favorites {

    /**
     * 使用Class对象为键的类型安全的异构容器,以这种方式使用的Class对象称作为类型令牌.
     */
    private Map<Class<?>, Object> favorites = new HashMap<Class<?>, Object>();

    public <T> void putFavorite(Class<T> type, T instance) {
        if (type == null)
            throw new NullPointerException("Type is null");
        /**
         * Class对象的cast方法可以将对象引用动态地转换成Class对象所表示的类型
         */
        favorites.put(type, type.cast(instance));
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }

}
