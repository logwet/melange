package me.logwet.melange.divine.filter;

public abstract class AbstractDivineFilter implements DivineFilter {
    protected abstract Policy getPolicy();

    protected abstract boolean tester(double x, int index);

    @Override
    public boolean test(double x, int index) {
        switch (this.getPolicy()) {
            case INCLUDE:
                return this.tester(x, index);
            case EXCLUDE:
                return !this.tester(x, index);
            default:
                return false;
        }
    }

    protected enum Policy {
        INCLUDE,
        EXCLUDE
    }
}
