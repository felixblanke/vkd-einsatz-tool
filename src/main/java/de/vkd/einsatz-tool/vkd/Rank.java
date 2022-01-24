package de.vkd.einsatz_tool.vkd;

public enum Rank {
    //ranks and their hierarchy
    VK(0), OVK(1), HVK(2), HVKA(3), UGL(4), PGL(5), GL(6), OGL(7), HGL(8), DEF(9);

    private int hierarchy;

    Rank(int hierarchy){
        this.hierarchy = hierarchy;
    }
    public int getHierarchy() {
        return hierarchy;
    }
}
