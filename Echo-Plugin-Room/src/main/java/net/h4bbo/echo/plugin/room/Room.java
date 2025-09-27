package net.h4bbo.echo.plugin.room;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;

public class Room implements AttributeMap {
    private static final AttributeMap attributeMap = new DefaultAttributeMap();

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
        return attributeMap.attr(attributeKey);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
        return attributeMap.hasAttr(attributeKey);
    }
}
