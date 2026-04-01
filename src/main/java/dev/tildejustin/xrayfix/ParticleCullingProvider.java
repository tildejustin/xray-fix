package dev.tildejustin.xrayfix;

public interface ParticleCullingProvider {

    Boolean xray_fix$shouldCull();

    void xray_fix$setCull(boolean cull);

}
