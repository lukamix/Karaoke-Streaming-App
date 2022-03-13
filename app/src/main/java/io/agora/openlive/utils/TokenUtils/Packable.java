package io.agora.openlive.utils.TokenUtils;

public interface Packable {
    ByteBuf marshal(ByteBuf out);
}
