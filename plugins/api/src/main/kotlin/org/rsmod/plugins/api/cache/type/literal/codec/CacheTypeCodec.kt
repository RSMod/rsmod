package org.rsmod.plugins.api.cache.type.literal.codec

public abstract class CacheTypeCodec<K, V>(public val out: Class<V>) {

    public abstract fun decode(value: K): V?

    public abstract fun encode(value: V): K
}
