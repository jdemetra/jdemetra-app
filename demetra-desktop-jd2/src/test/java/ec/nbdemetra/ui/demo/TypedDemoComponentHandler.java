/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.demo;

import java.awt.Component;
import javax.swing.JToolBar;

/**
 *
 * @author CHARPHI
 */
public abstract class TypedDemoComponentHandler<T> implements DemoComponentHandler {
    
    private final Class<T> clazz;

    public TypedDemoComponentHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void doConfigure(T c) {
    }

    public void doFillToolBar(JToolBar toolBar, T c) {
    }

    @Override
    public final boolean canHandle(Component c) {
        return clazz.isInstance(c);
    }

    @Override
    public final void configure(Component c) {
        doConfigure(clazz.cast(c));
    }

    @Override
    public final void fillToolBar(JToolBar toolBar, Component c) {
        doFillToolBar(toolBar, clazz.cast(c));
    }
}
