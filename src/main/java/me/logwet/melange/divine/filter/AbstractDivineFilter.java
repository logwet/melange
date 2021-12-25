package me.logwet.melange.divine.filter;

public abstract class AbstractDivineFilter implements DivineFilter {
    protected abstract Policy getPolicy();

    protected abstract boolean tester(double x);

    @Override
    public boolean test(double x) {
        switch (this.getPolicy()) {
            case INCLUDE:
                return this.tester(x);
            case EXCLUDE:
                return !this.tester(x);
            default:
                return false;
        }
    }

    protected enum Policy {
        INCLUDE,
        EXCLUDE
    }
}
