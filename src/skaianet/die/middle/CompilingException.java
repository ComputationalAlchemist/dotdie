package skaianet.die.middle;

public class CompilingException extends Exception {
    private String traceInfo;

    public CompilingException(String message) {
        super(message);
    }

    public String toString() {
        return traceInfo == null ? super.toString() : super.toString() + " at " + traceInfo;
    }

    public void addTrace(String traceInfo) {
        if (this.traceInfo == null) {
            this.traceInfo = traceInfo;
        }
    }
}
