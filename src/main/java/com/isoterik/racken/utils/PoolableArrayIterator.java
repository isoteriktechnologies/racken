package com.isoterik.racken.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class PoolableArrayIterator<T> extends Array.ArrayIterator<T> implements Pool.Poolable {
    public PoolableArrayIterator(Array<T> array) {
        super(array);
    }

    public PoolableArrayIterator(Array<T> array, boolean allowRemove) {
        super(array, allowRemove);
    }
}
