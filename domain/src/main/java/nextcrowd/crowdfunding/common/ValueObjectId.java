package nextcrowd.crowdfunding.common;

public abstract class ValueObjectId implements Comparable<ValueObjectId> {
    private final String value;

    protected ValueObjectId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    public String id(){
        return value;
    }

    @Override
    public int compareTo(ValueObjectId other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueObjectId that = (ValueObjectId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
