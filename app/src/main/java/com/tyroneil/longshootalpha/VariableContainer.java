package com.tyroneil.longshootalpha;

public class VariableContainer<Type> {
    private Type variable;

    public VariableContainer(Type variable) {
        this.variable = variable;
    }

    public void setVariable(Type variable) {
        this.variable = variable;
    }

    public Type getVariable() {
        return variable;
    }
}
