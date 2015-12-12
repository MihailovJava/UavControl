package mavlink.control.conditions


interface Condition<T> {
    boolean isOk(T t)
    Class<T> getType()
}