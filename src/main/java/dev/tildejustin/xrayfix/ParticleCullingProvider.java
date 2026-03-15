package dev.tildejustin.xrayfix;

public interface ParticleCullingProvider {

    boolean xray_fix$shouldCull();

    void xray_fix$setCull(boolean cull);

}
