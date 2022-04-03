package demetra.desktop.util;

@lombok.Value
public class ComponentDescriptor {

    private String name;
    private int cmp;
    private boolean signal;
    private boolean lowFrequency;
}
