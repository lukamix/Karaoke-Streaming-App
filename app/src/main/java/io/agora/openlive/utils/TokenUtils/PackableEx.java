package io.agora.openlive.utils.TokenUtils;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
