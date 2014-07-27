package skaianet.die.middle;

/**
 * Created on 2014-07-24.
 */
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
