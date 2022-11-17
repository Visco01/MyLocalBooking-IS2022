package uni.project.mylocalbooking.api;

interface RunOnResponse<T> {
    void apply(T data);
}
