package com.tyroneil.longshootalpha;

public class Support_VariableContainer<Type> {
    private Type variable;

    public Support_VariableContainer(Type variable) {
        this.variable = variable;
    }

    public void setVariable(Type variable) {
        this.variable = variable;
    }

    public Type getVariable() {
        return variable;
    }
}
