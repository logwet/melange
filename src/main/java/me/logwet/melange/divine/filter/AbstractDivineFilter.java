package me.logwet.melange.divine.filter;

public abstract class AbstractDivineFilter implements DivineFilter {
    protected final Policy policy;

    public AbstractDivineFilter(Policy policy) {
        this.policy = policy;
    }

    @Override
    public Policy getPolicy() {
        return policy;
    }
}
